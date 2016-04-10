package me.aeternussamurai.frequencymusicplayer.glide;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.module.GlideModule;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Chase on 4/2/2016.
 */
public class MusicCoverModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(MusicCover.class, InputStream.class, new MusicCoverLoader.Factory());
    }
}

class MusicCoverLoader implements StreamModelLoader<MusicCover> {

    @Override
    public DataFetcher<InputStream> getResourceFetcher(MusicCover model, int width, int height) {
        return new MusicCoverFetcher(model);
    }

    static class Factory implements ModelLoaderFactory<MusicCover, InputStream>{

        @Override
        public ModelLoader<MusicCover, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new MusicCoverLoader();
        }

        @Override
        public void teardown() {

        }
    }
}

class MusicCoverFetcher implements DataFetcher<InputStream>{
    private final MusicCover model;
    private FileInputStream stream;

    public MusicCoverFetcher(MusicCover model){
        this.model = model;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        MediaMetadataRetriever ret = new MediaMetadataRetriever();
        try{
            ret.setDataSource(model.path);
            byte[] cover = ret.getEmbeddedPicture();
            if(cover != null){
                return new ByteArrayInputStream(cover);
            }else{
                return null;
            }
        }finally {
            ret.release();
        }
    }

    @Override
    public void cleanup() {
        if(stream != null){
            try{
                stream.close();
            }catch (IOException e){
                //meh
            }
        }
    }

    @Override
    public String getId() {
        return model.path;
    }

    @Override
    public void cancel() {

    }
}