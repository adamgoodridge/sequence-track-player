package net.adamgoodridge.sequencetrackplayer.shortcut;

import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class ShortcutService {
    private final ShortcutRepository shortcutRepository;

    private final NasConnectorService nasConnectorService;

    public ShortcutService(ShortcutRepository shortcutRepository) {
        this.shortcutRepository = shortcutRepository;
        this.nasConnectorService = new NasConnectorFileSystem();
    }

    public List<Shortcut> getShortcuts() {
        return shortcutRepository.findAll();
    }

    public void saveShortcuts(String[] names) {
        shortcutRepository.deleteAll();
        for(String name : names) {
            String path = nasConnectorService.logoPath(name);
            Shortcut shortcut = new Shortcut(name, path);
            shortcutRepository.save(shortcut);
        }
    }
}
