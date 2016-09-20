package com.mobiapp.nicewallpapers.fragments;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobiapp.nicewallpapers.R;
import com.mobiapp.nicewallpapers.model.OEMDetail;
import com.mobiapp.nicewallpapers.model.PhotoGoup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import uk.co.senab.photoview.PhotoView;

@EFragment(R.layout.image_detail_fragment)
public class ImageDetailFragment extends Fragment {

    @ViewById(R.id.imgDetail)
    protected PhotoView mImageView;
    @ViewById(R.id.loading)
    protected ProgressBar mProgressBar;

    @FragmentArg
    protected PhotoGoup mPhoto;
    @FragmentArg
    protected OEMDetail mOemDetail;

    @AfterViews
    void onCreate() {
        if (null != mPhoto && null != mPhoto.getFile()) {
            mProgressBar.setVisibility(View.GONE);
            mImageView.setImageURI(Uri.fromFile(mPhoto.getFile()));
        } else {
            String urlImage;
            if (mOemDetail == null) {
                urlImage = String.format(getActivity().getString(R.string.image_url),
                        mPhoto.getFarm(), mPhoto.getServer(), mPhoto.getId(), mPhoto.getSecret());
            } else {
                urlImage = String.format(getActivity().getString(R.string.base_url_oem_photo),
                        mOemDetail.getThumb());
            }
            Glide.with(getActivity()).load(urlImage)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .dontAnimate()
                    .into(mImageView);
        }
    }
}
