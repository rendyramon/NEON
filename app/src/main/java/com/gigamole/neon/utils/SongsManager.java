package com.gigamole.neon.utils;
/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.widget.TextView;
import com.gigamole.neon.models.SongModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by GIGAMOLE on 17.05.2015.
 */
public class SongsManager {

    private final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/";

    private final MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
    private final BitmapFactory.Options purgableOptions = new BitmapFactory.Options();
    private byte[] tempCover;

    public void getPlayList(ArrayList<String> songsList) {
        this.purgableOptions.inPurgeable = true;

        File home = new File(this.MEDIA_PATH);
        File[] listFiles = home.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    scanDirectory(file, songsList);
                } else {
                    addSongToList(file, songsList);
                }
            }
        }
    }

    private void scanDirectory(File directory, ArrayList<String> songsList) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file, songsList);
                    } else {
                        addSongToList(file, songsList);
                    }
                }
            }
        }
    }

    private void addSongToList(File song, ArrayList<String> songsList) {
        if (song.getName().endsWith(".mp3")) {
            songsList.add(song.getAbsolutePath());
        }
    }

    public void initPlayList(ArrayList<String> pathsList, ArrayList<SongModel> songsList, TextView textView) {

        for (String path : pathsList) {
            this.metadataRetriever.setDataSource(path);

            try {
                this.tempCover = this.metadataRetriever.getEmbeddedPicture();

                Bitmap bitmap = null;
                if (this.tempCover != null) {
                    bitmap = BitmapFactory.decodeByteArray(this.tempCover, 0, this.tempCover.length, this.purgableOptions);
                }

                songsList.add(new SongModel(
                                this.metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                                this.metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                                this.metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                                path,
                                bitmap
                        )
                );
                textView.setText(String.format("%d/%d", songsList.size(), pathsList.size()));

            } catch (Exception e) {
            }
        }
    }
}
