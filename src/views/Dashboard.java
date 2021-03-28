package views;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaView;
import models.Session;

public class Dashboard extends View {

    public Button pauseBtn;
    public Button playBtn;
    public Slider songScrubber;
    public ImageView albumCover2Iv;
    public Label songName2Lb;
    public Label artistLb2;
    public Slider volumeScrubber;
    public Label currentTimeLb;
    public Label totalTimeLb;
    public Button prevBtn;
    public Button nextBtn;
    public Button repeatBtn;
    public Button shuffleBtn;
    public Button addSongBtn;
    public TextField searchSongsTf;
    public Label songNameLb;
    public Label artistLb;
    public Label albumLb;
    public TabPane userTabPane;
    public TableView songsListTv;
    public ImageView albumCoverIv;
    public MediaView mediaView;
    public TableView queueSongTv;

    public TableView albumTv;
    public TextField searchAlbumsTf;
    public Button newAlbumBtn;
    public TableView albumSongsTv;

    public TableView playlistTv;
    public TextField searchPlaylistTf;
    public Button newPlaylistBtn;
    public TableView playListSongsTv;

    public TableView usersTv;

    public Tab queueTab;

    public TabPane profileTabPane;
    public Button logoutBtn;

    public Button followBtn;
    public Button unfollowBtn;

    public TextField searchArtistsTf;

    public TableView listenersTv;
    public TextField searchListenerTf;

    public TabPane searchTabPane;

    public Label usernameLb;
    public Label emailLb;
    public Label bdayLb;

    public ListView followsLv;
    public Pane followsPane;

    public ListView followersLv;
    public Pane followersPane;

    public Label followersCountLb;

    public TableView activityTv;
    public TableView notificationsTv;
    public TableView likedsongsTv;
    public TextField searchLikedSongsTf;

    public Button repeatOneBtn;

    public TableView highlightsTv;
    public TextField searchHighlightsTf;

}
