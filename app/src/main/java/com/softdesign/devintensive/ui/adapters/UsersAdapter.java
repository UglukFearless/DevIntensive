package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.ui.view.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ugluk on 15.07.2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private Context mContext;
    private List<UserListRes.UserData> mUsers;
    private UserViewHolder.CustomClickListener mCustomClickListener;

    public UsersAdapter(List<UserListRes.UserData> users, UserViewHolder.CustomClickListener customClickListener) {
        mUsers = users;

        mCustomClickListener = customClickListener;
    }

    @Override
    public UsersAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(convertView, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.UserViewHolder holder, int position) {
        UserListRes.UserData user = mUsers.get(position);

        Picasso.with(mContext)
                .load(user.getPublicInfo().getPhoto())
                .placeholder(mContext.getResources().getDrawable(R.drawable.header_bg))
                .error(mContext.getResources().getDrawable(R.drawable.header_bg))
                .into(holder.userPhoto);

        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(user.getProfileValues().getRating());
        holder.mCodeLines.setText(user.getProfileValues().getLinesCode());
        holder.mProjects.setText(user.getProfileValues().getProjects());

        if (user.getPublicInfo().getBio()!=null&&!user.getPublicInfo().getBio().isEmpty()) {
            holder.mBio.setText(user.getPublicInfo().getBio());
            holder.mBio.setVisibility(View.VISIBLE);
        } else  {
            holder.mBio.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.user_photo_img)
        AspectRatioImageView userPhoto;
        @BindView(R.id.user_full_name_txt)
        TextView mFullName;
        @BindView(R.id.rating_txt)
        TextView mRating;
        @BindView(R.id.code_lines_txt)
        TextView mCodeLines;
        @BindView(R.id.projects_txt)
        TextView mProjects;
        @BindView(R.id.bio_txt)
        TextView mBio;
        @BindView(R.id.btn_more_info)
        Button mShowMore;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);

            mListener = customClickListener;
            ButterKnife.bind(itemView);

            mShowMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener!=null) {
                mListener.onUserItemClickListener(getAdapterPosition());
            }
        }

        public interface CustomClickListener {

            void onUserItemClickListener(int position);

        }
    }
}
