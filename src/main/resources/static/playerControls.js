
//todo set pause where prev is press & the current track is paused
function playerStatusChange() {
    if(player.paused === false) {
        player.pause();
        playControlButton.textContent = "Play";
    } else {
        player.play();
        playControlButton.textContent = "Pause";
    }
}

function setAllElements(idAgruement) {
    timerText = document.getElementById("timer" + idAgruement);
    titleText = document.getElementById("title" + idAgruement);
    trackCountText = document.getElementById("trackCount" + idAgruement);
    statusText = document.getElementById("statusTitle" + idAgruement);
    playControlButton = document.getElementById("playControlButton" + idAgruement);
    previousButton = document.getElementById("previousButton" + idAgruement);
    nextButton = document.getElementById("nextButton" + idAgruement);
    bookmarkButton = document.getElementById("bookmarkButton" + idAgruement);
    titleLink = document.getElementById("titleLink" + idAgruement);
}
    function showError(errorText) {
        updatePlayerButtonsHidden(true);
         statusText.innerHTML = errorText;
        document.getElementById("myAudio").hidden = true;
        player.pause();

    bookmarkId = -1;
    if (errorText === "end of files") {
        previousButton.hidden = false;
    } else if (errorText === "Gone back to the start of the file") {
        nextButton.hidden = false;
    }
}

function loadDataIntoPlayer(data, isPlay) {

    if (data.hasOwnProperty("error")) {
        showError(data["error"]);
    } else {
        updatePlayerControlsHidden(false);
        titleText.innerHTML = data["trackName"];
        document.getElementById("myAudio").src = data["trackUrl"];
        document.getElementById("browserLink").href = "/browser/feed/" + data["feedId"];
        titleLink.setAttribute("href", data["trackUrl"]);
        if(trackCountText) {
            trackCountText.innerHTML = "current track count: " + data["trackCurrentCount"]; 
        }
        //when the shuffle pages loads and no feeds is selected therefore the single button is disabled
        document.getElementById("browserButton").disabled = false;
        const title = data["feedName"];
        if (isScanning) {
            document.title = "Scanning - " + title;
        } else {
            document.title = "Shuffle - " + title;
        }
        bookmarkId = data["bookmarkId"];
        player.load();
        player.currentTime = data["currentPosition"];
        //if playing auto from the last track
        if (isPlay) {
            playControlButton.textContent = "Pause";
            player.play();
        } else {

            playControlButton.textContent = "Play";
            if (Math.floor(player.duration) === 0) {
                changeTrack(lastUrl);
                return;
            }
        }
        //wait until media is loaded
        player.addEventListener('loadeddata', ev => {
            updateBookmarksButton();
            updateLengthTimeText();
        });
    }

}

function updatePlayerControlsHidden(isHidden) {
    timerText.hidden = isHidden
    titleText.hidden = isHidden;
    statusText.hidden = !isHidden;
    trackCountText
    updatePlayerButtonsHidden(isHidden);
}

function updatePlayerButtonsHidden(isHidden) {
    playControlButton.hidden = isHidden;
    previousButton.hidden = isHidden;
    nextButton.hidden = isHidden;
    bookmarkButton.hidden = isHidden;
}

//stops a requeat runing more than once
let aborter = null;
function updateLengthTimeText(titleId) {
    let currentLength = Math.floor(player.currentTime);
    let totalDuration = player.duration;
    if (totalDuration === "NaN") {
        totalDuration = secondToTime(Math.round(totalDuration));
    } else if (totalDuration === "Infinity") {
        //safari of get file
        let url = "/feed/logging/infinity_error_happened";
        $.getJSON(url, function (data) {

        });
        url = "/feed/text/get/current/" + feedId + "?sessionId=" + sessionId;
        $.getJSON(url, function (data) {
            loadDataIntoPlayer(data, false);
        });

    }
    
    timerText.innerHTML = secondToTime(currentLength) + "/" + secondToTime(Math.round(totalDuration));
    const update_url = "/feed/text/update/length/" + feedId    + "/" + currentLength + "?sessionId=" + sessionId;
    if(aborter) aborter.abort();

    aborter = new AbortController();
    const singal = aborter.singal;
    $.getJSON(update_url,{singal}).then(data => {
        //if it isn't the not right session
        aborter == null;
        if (data.hasOwnProperty("error")) {
            updatePlayerControlsHidden(true);
            player.pause();
            //document.getElementById("statusTitle" + titleId).innerHTML = data["error"];
            titleText.innerHTML = data["feedName"];
            titeText.hidden = false;
            updatePlayerButtonsHidden(true);
            statusText.innerHTML = data["error"];
            statusText.hidden = false;
        }
    });
}

function secondToTime(total) {
    const minutes = Math.floor(total / 60);
    let seconds = total % 60;
    if (seconds < 10) {
        seconds = "0" + seconds;
    }
    return minutes + ":" + seconds;
}

function bookmarkButtonClick() {
    //add or remove bookmark
    if(bookmarkId == -1) {
        addBookmark();
    } else {
        deleteBookmark();
    }
}

function addBookmark() {
    const add_bookmark_url = "/bookmark/json/add/" + feedId;
    $.getJSON(add_bookmark_url, function (data) {
        bookmarkId = data["bookmarkId"];
        //handle the error -1
        updateBookmarksButton();
    })
}

function deleteBookmark() {
    const url = "/bookmark/json/delete/" + bookmarkId;
    $.getJSON(url, function (data) {
        const excepted = "Bookmark id (" + bookmarkId + ") successful deleted in the database!!";
        if (data["status"] === excepted) {
            bookmarkId = -1;
            updateBookmarksButton();
        }
    });
}

function changeSetting(settingName, buttonId) {
    const replaceValue = !settings.get(settingName);
    const url = "/setting/set/" + settingName + "/" + replaceValue;var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 204) {
            settings.set(settingName, replaceValue)
            updateSettingButton(buttonId, replaceValue);
        }
    };
    xhttp.open("PUT", url, true);
    xhttp.send();
}

function updateBookmarksButton() {
    if (bookmarkId.toString() === "-1") {
        //if it doesn't has a bookmark
        bookmarkButton.className = "btn btn-primary btn-lg btn-block";
        bookmarkButton.innerHTML = "Add Bookmark";
    } else {
        bookmarkButton.className = "btn btn-danger btn-lg btn-block";
        bookmarkButton.innerHTML = "Remove Bookmark";
    }
}

function updateSettingButton(buttonId, value) {
    let turnShuffleButton = document.getElementById(buttonId);
    const displayName = turnShuffleButton.getAttribute("name");
    if (value) {
        turnShuffleButton.className = "btn btn-danger btn-lg btn-block";
        turnShuffleButton.innerHTML = `Turn Off ${displayName}`;
    } else {
        turnShuffleButton.className = "btn btn-primary btn-lg btn-block";
        turnShuffleButton.innerHTML = `Turn On ${displayName}`;
    }
    //turnShuffleButton.onclick = function () {turnShuffle(!isScanning)};
}
//parse int