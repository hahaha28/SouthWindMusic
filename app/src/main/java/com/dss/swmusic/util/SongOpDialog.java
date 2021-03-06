package com.dss.swmusic.util;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dss.swmusic.R;
import com.dss.swmusic.cache.MeCache;
import com.dss.swmusic.custom.dialog.BottomSheetDialog;
import com.dss.swmusic.custom.dialog.ListDialog;
import com.dss.swmusic.custom.dialog.NewPlaylistDialog;
import com.dss.swmusic.entity.PlayerSong;
import com.dss.swmusic.entity.UserBaseData;
import com.dss.swmusic.event.PlayListUpdateEvent;
import com.dss.swmusic.network.OkCallback;
import com.dss.swmusic.network.ServiceCreator;
import com.dss.swmusic.network.SongService;
import com.dss.swmusic.network.bean.NewPlayListResult;
import com.dss.swmusic.network.bean.PlayList;
import com.dss.swmusic.network.bean.Result;
import com.dss.swmusic.network.bean.UpdatePlaylistResult;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import fun.inaction.dialog.BottomDialog;
import fun.inaction.dialog.dialogs.IconTextListDialog;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongOpDialog {

    private SongService songService = ServiceCreator.INSTANCE.create(SongService.class);

    private PlayerSong song;
//    private List<PlayerSong> songList;
    private BottomSheetDialog bottomDialog;
    private long playListId = 0;

    public SongOpDialog(Context context, PlayerSong song,long playListId) {
        this.song = song;
//        this.songList = songList;
        this.playListId = playListId;

        initDialog(context);
    }

    public SongOpDialog(Context context,PlayerSong song){
        this.song = song;
        initDialog(context);
    }

    private void initDialog(Context context) {
        bottomDialog = new BottomSheetDialog(context);

        List<Pair<Integer, String>> items = new ArrayList<>();
        items.add(new Pair(R.drawable.ic_song_next_play, "???????????????"));
        items.add(new Pair(R.drawable.ic_collect, "???????????????"));
        items.add(new Pair(R.drawable.ic_download, "??????"));
        items.add(new Pair(R.drawable.ic_comment, "??????"));
        if(playListId != 0){
            items.add(new Pair(R.drawable.ic_delete, "??????"));
        }

        bottomDialog.setHeader(song.getAlbums().getPicUrl()
                , song.getName()
                , ExtensionKt.toNiceString(song.getArtists()))
                .setListData(items);

        bottomDialog.setOnItemClickListener((adapter, view, position) -> {
            switch (position) {
                // ???????????????
                case 0:
                    SongPlayer.setNextPlay(song);
                    bottomDialog.dismiss();
                    break;

                // ???????????????
                case 1:
                    bottomDialog.dismiss();
                    collectToPlaylist(context);
                    break;

                // ??????
                case 2:
                    ExtensionKt.showNoImplementDialog(context);
                    bottomDialog.dismiss();
                    break;

                // ??????
                case 3:
                    ExtensionKt.showNoImplementDialog(context);
                    bottomDialog.dismiss();
                    break;

                // ??????
                case 4:
                    deleteFromPlaylist();
                    bottomDialog.dismiss();
                    break;

            }
        });
    }

    // ???????????????
    private void collectToPlaylist(Context context) {

        // ????????????????????????
        List<PlayList> playLists = new ArrayList<>();
        MeCache meCache = new MeCache();
        meCache.getLikedPlayListCache(playList -> {
            playLists.add(playList);
            meCache.getCreatedPlayList(playLists1 -> {
                playLists.addAll(playLists1);

                // ??????????????????

                // ????????????????????????????????????
                List<Pair<String, String>> items = new ArrayList<>();
                items.add(new Pair("http://img.inaction.fun/static/29640.png", "????????????"));
                for (PlayList p : playLists) {
                    items.add(new Pair(p.getCoverImgUrl(), p.getName()));
                }
                ListDialog dialog = new ListDialog(context);
                dialog.setTitle("???????????????")
                        .setData(items)
                        .setOnItemClickListener((adapter, view, position) -> {
                            if (position != 0) {
                                songService.addToPlaylist(playLists.get(position - 1).getId()
                                        , song.getId()
                                        , UserBaseDataUtil.getCookie())
                                        .enqueue(new Callback<UpdatePlaylistResult>() {
                                            @Override
                                            public void onResponse(Call<UpdatePlaylistResult> call, Response<UpdatePlaylistResult> response) {
                                                UpdatePlaylistResult result = response.body();
                                                if (result.getStatus() == 200) {
                                                    ToastUtilKt.showToast("????????????");
                                                    notifyPlayListUpdate(playLists.get(position - 1).getId());
                                                } else {
                                                    ToastUtilKt.showToast("??????????????????status: " + result.getStatus());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<UpdatePlaylistResult> call, Throwable t) {
                                                ToastUtilKt.showToast("????????????");
                                            }
                                        });
                                dialog.dismiss();
                            } else {
                                NewPlaylistDialog newPlaylistDialog = new NewPlaylistDialog(context);
                                newPlaylistDialog.setOnCancelBtnClickListener(v -> {
                                    newPlaylistDialog.dismiss();
//                                    dialog.dismiss();
                                }).setOnConfirmBtnClickListener(v -> {
                                    String content = newPlaylistDialog.getEditText();
                                    if(content.equals("")){
                                        ToastUtilKt.showToast("?????????????????????");
                                    }else{
                                        // ??????????????????????????????
                                        songService.newPlaylist(UserBaseDataUtil.getCookie(),content)
                                                .enqueue(new OkCallback<NewPlayListResult>(){
                                                    @Override
                                                    public void onSuccess(@NotNull NewPlayListResult result) {
                                                        super.onSuccess(result);
                                                        songService.addToPlaylist(result.getId()
                                                                , song.getId()
                                                                , UserBaseDataUtil.getCookie())
                                                                .enqueue(new Callback<UpdatePlaylistResult>() {
                                                                    @Override
                                                                    public void onResponse(Call<UpdatePlaylistResult> call, Response<UpdatePlaylistResult> response) {
                                                                        UpdatePlaylistResult result = response.body();
                                                                        if (result.getStatus() == 200) {
                                                                            ToastUtilKt.showToast("????????????");
                                                                            notifyPlayListUpdate(result.getId());
                                                                        } else {
                                                                            ToastUtilKt.showToast("??????????????????status: " + result.getStatus());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(Call<UpdatePlaylistResult> call, Throwable t) {
                                                                        ToastUtilKt.showToast("????????????");
                                                                    }
                                                                });
                                                    }
                                                });
                                        newPlaylistDialog.dismiss();
                                        dialog.dismiss();
                                    }

                                }).show();
                            }
                        })
                        .show();


                return null;
            });
            return null;
        });


    }

    // ??????????????????
    private void deleteFromPlaylist(){
        songService.deleteFromPlaylist(playListId,song.getId(),UserBaseDataUtil.getCookie())
                .enqueue(new Callback<UpdatePlaylistResult>() {
                    @Override
                    public void onResponse(Call<UpdatePlaylistResult> call, Response<UpdatePlaylistResult> response) {
                        UpdatePlaylistResult result = response.body();
                        if(result.getStatus() == 200){
                            ToastUtilKt.showToast("????????????");
                            notifyPlayListUpdate(playListId);
                        }else{
                            ToastUtilKt.showToast("???????????????,status:"+result.getStatus());
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdatePlaylistResult> call, Throwable t) {
                        ToastUtilKt.showToast("????????????");
                    }
                });
    }

    // ??????????????????
    private void notifyPlayListUpdate(long updatePlayListId){
        EventBus.getDefault().post(new PlayListUpdateEvent(updatePlayListId));
    }

    public void show() {

        bottomDialog.show();

    }

}
