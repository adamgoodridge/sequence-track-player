package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.DataItem;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.GetIndexByRandomStrategy;
import net.adamgoodridge.sequencetrackplayer.filesystem.Path;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetIndexByRandomStrategyTest extends AbstractSpringBootTest {

	@Autowired
	private SettingRepository settingRepository;

	@Mock private IDirectoryRepository directoryRepository;
	@Mock private RandomNumberGenerator rnd;

	@BeforeEach
	void setUp() throws IOException {
		LoadClassDef.initializeComponents();
		MockitoAnnotations.openMocks(this);
		new SettingRepositoryMock().fillWithMockData(settingRepository);
	}

	// ── helpers ──────────────────────────────────────────────────────────────

	private GetIndexByRandomStrategy strategy(PreferredRandomSettings settings) {
		return new GetIndexByRandomStrategy(settings, rnd);
	}

	private PreferredRandomSettings anyDay() {
		return PreferredRandomSettings.builder().build();
	}

	private PreferredRandomSettings withDay(String day) {
		return PreferredRandomSettings.builder().dayOfWeek(day).build();
	}

	private PreferredRandomSettings withTime(int hour) {
		return PreferredRandomSettings.builder().time(hour).build();
	}

	/**
	 * Resolves a logical path to the fully-qualified string that Path.toString() produces.
	 * e.g. "feeds/FeedA" → "/mnt/path/feeds/FeedA"
	 * Use this for both givenDirectory() and feeder() to ensure they always agree.
	 */
	private static String resolve(String logicalPath) {
		return new Path(logicalPath).toString();
	}

	/**
	 * Builds a real RetrieveAudioFeeder backed by the mocked IDirectoryRepository,
	 * then calls the protected getFilesByDataItemInCurrentPath() so that subFiles and
	 * folders are populated — exactly as compute() would do — without running the full loop.
	 */
	private RetrieveAudioFeeder feeder(String logicalPath, GetIndexByRandomStrategy strategy) {
		RetrieveAudioFeeder feeder = new RetrieveAudioFeeder(logicalPath, strategy, directoryRepository);
		feeder.currentPath = feeder.searchFor; // searchFor is already the correctly resolved Path
		feeder.getFilesByDataItemInCurrentPath();
		feeder.folders = feeder.filterByDirectories();
		return feeder;
	}

	/**
	 * Stubs directoryRepository for the resolved path.
	 * Names ending in .mp3/.m4a are treated as files; everything else as a folder.
	 */
	private void givenDirectory(String logicalPath, String... subFileNames) {
		when(directoryRepository.findDirectoryByNameEquals(resolve(logicalPath)))
				.thenReturn(new Directory(resolve(logicalPath), subFileNames));
	}

	// ── getFolderIndex ────────────────────────────────────────────────────────

	@Test
	void getFolderIndex_NoNextPath_NoDayFilter_ReturnsRandomIndex() throws GetFeedError {
		givenDirectory("feeds/FeedA", "FeedA");
		GetIndexByRandomStrategy s = strategy(anyDay());
		RetrieveAudioFeeder f = feeder("feeds/FeedA", s);
		when(rnd.getRandomNumber(1)).thenReturn(0);

		int result = s.getFolderIndex(f);

		assertEquals(0, result);
		verify(rnd).getRandomNumber(1);
	}

	@Test
	void getFolderIndex_DayFilterMatches_ReturnsMatchingFolderIndex() throws GetFeedError {
		givenDirectory("feeds/Monday_FeedA", "Monday_FeedA", "Tuesday_FeedB");
		GetIndexByRandomStrategy s = strategy(withDay("Monday"));
		RetrieveAudioFeeder f = feeder("feeds/Monday_FeedA", s);
		when(rnd.getRandomNumber(1)).thenReturn(0);

		int result = s.getFolderIndex(f);

		assertEquals(0, result);
	}

	@Test
	void getFolderIndex_DayFilterNoMatch_ThrowsGetFeedError() {
		givenDirectory("feeds/Tuesday_FeedB", "Tuesday_FeedB");
		GetIndexByRandomStrategy s = strategy(withDay("Monday"));
		RetrieveAudioFeeder f = feeder("feeds/Tuesday_FeedB", s);


		GetRandomFeedError error = assertThrows(GetRandomFeedError.class, () -> s.getFolderIndex(f));

		String exceptedMessage = "Cannot find random track for /mnt/path/feeds/Tuesday_FeedB, Reason: Cannot find folder for day: MONDAY";
		assertEquals(exceptedMessage, error.getMessage());
	}

	// ── getAudioFileIndex ─────────────────────────────────────────────────────

	@Test
	void getAudioFileIndex_NoTimePreference_ReturnsRandomIndex() {
		givenDirectory("feeds/FeedA", "track.mp3");
		GetIndexByRandomStrategy s = strategy(anyDay());
		RetrieveAudioFeeder f = feeder("feeds/FeedA", s);
		when(rnd.getRandomNumber(1)).thenReturn(0);

		int result = s.getAudioFileIndex(f);

		assertEquals(0, result);
	}

	@Test
	void getAudioFileIndex_TimePreference_PathContainsSpace_ReturnsRandomIndex() {
		givenDirectory("feeds/Feed A", "track.mp3");
		GetIndexByRandomStrategy s = strategy(withTime(9));
		RetrieveAudioFeeder f = feeder("feeds/Feed A", s);
		when(rnd.getRandomNumber(1)).thenReturn(0);

		int result = s.getAudioFileIndex(f);

		assertEquals(0, result);
	}

	@Test
	void getAudioFileIndex_TimePreference_MatchFound_ReturnsMatchingIndex() {
		givenDirectory("feeds/FeedA", "track_08-30.mp3", "track_09-00.mp3");
		GetIndexByRandomStrategy s = strategy(withTime(9));
		RetrieveAudioFeeder f = feeder("feeds/FeedA", s);
		when(rnd.getRandomNumber(1)).thenReturn(0);

		int result = s.getAudioFileIndex(f);

		assertEquals(1, result);
	}

	@Test
	void getAudioFileIndex_TimePreference_NoMatchAtAnyHour_FallsBackToRandom() {
		givenDirectory("feeds/FeedA", "track_no_time.mp3");
		GetIndexByRandomStrategy s = strategy(withTime(3));
		RetrieveAudioFeeder f = feeder("feeds/FeedA", s);
		when(rnd.getRandomNumber(1)).thenReturn(0);

		int result = s.getAudioFileIndex(f);

		assertEquals(0, result);
	}

	@Test
	void getAudioFileIndex_TimePreference_ToFormatFile_UsesWildcardRegex() {
		givenDirectory("feeds/FeedA", "track_09_TO_10.mp3");
		GetIndexByRandomStrategy s = strategy(withTime(9));
		RetrieveAudioFeeder f = feeder("feeds/FeedA", s);
		when(rnd.getRandomNumber(1)).thenReturn(0);

		int result = s.getAudioFileIndex(f);

		assertEquals(0, result);
	}


	// ── pickRandomItemPerRegex ────────────────────────────────────────────────

	@Test
	void pickRandomItemPerRegex_NoMatch_ReturnsMinusOne() {
		givenDirectory("feeds/FeedA", "track.mp3");
		GetIndexByRandomStrategy s = strategy(anyDay());
		RetrieveAudioFeeder f = feeder("feeds/FeedA", s);
		List<DataItem> subFiles = f.getSubFiles();

		int result = s.pickRandomItemPerRegex(".*NOMATCH.*", subFiles);

		assertEquals(-1, result);
		verifyNoInteractions(rnd);
	}

	@Test
	void pickRandomItemPerRegex_MultipleMatches_ReturnsCorrectGlobalIndex() {
		givenDirectory("feeds/FeedA",
				"track_Monday_01.mp3", "track_Monday_02.mp3", "track_Tuesday_01.mp3");
		GetIndexByRandomStrategy s = strategy(anyDay());
		RetrieveAudioFeeder f = feeder("feeds/FeedA", s);
		s.getAudioFileIndex(f); // prime internal retrieveAudioFeeder reference
		List<DataItem> subFiles = f.getSubFiles();

		// two Monday files match; rnd picks index 1 within matching subset → global index 1
		when(rnd.getRandomNumber(2)).thenReturn(1);
		int result = s.pickRandomItemPerRegex(".*Monday.*", subFiles);

		assertEquals(1, result);
	}
}

