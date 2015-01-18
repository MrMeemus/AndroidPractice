package com.practice.rquan24.criminalintent;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v4.app.DialogFragment;

/**
 * Created by rquan24 on 1/11/15.
 */
public class ImageFragment extends DialogFragment
{
    public static final String EXTRA_IMAGE_PATH = "com.practice.rquan24.criminalintent.image_path";

    public static ImageFragment newInstance(String imagePath)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);

        // NO TITLe is a minimalist look
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    /*
        Since image Fragment will not need the title or the buttons provided by
        alertDialog we only overrid onCreateView with a simple view rather
        than overrid onCreateDialog.

        OnCreateView I am creating and getting an imageview from scratch and retrieve
        the path from its arguments. Then get a scaled version of the image and set it
        on the ImageView.
     */

    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        mImageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);

        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }

}












