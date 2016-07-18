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

import java.util.List;


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

        if (!(user.getPublicInfo().getPhoto().isEmpty())) {
            Picasso.with(mContext)
                    .load(user.getPublicInfo().getPhoto())
                    .placeholder(mContext.getResources().getDrawable(R.drawable.nav_header_bg))
                    .error(mContext.getResources().getDrawable(R.drawable.nav_header_bg))
                    .into(holder.userPhoto);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.nav_header_bg)
                    .placeholder(mContext.getResources().getDrawable(R.drawable.nav_header_bg))
                    .error(mContext.getResources().getDrawable(R.drawable.nav_header_bg))
                    .into(holder.userPhoto);
        }



        holder.mFullName.setText(user.getFullName());
        holder.mRating.setText(String.valueOf(user.getProfileValues().getRating()));
        holder.mCodeLines.setText(String.valueOf(user.getProfileValues().getLinesCode()));
        holder.mProjects.setText(String.valueOf(user.getProfileValues().getProjects()));

        if (user.getPublicInfo().getBio()!=null&&!user.getPublicInfo().getBio().isEmpty()) {
            holder.mBio.setText(user.getPublicInfo().getBio());
            holder.mBio.setVisibility(View.VISIBLE);
        } else  {
            holder.mBio.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AspectRatioImageView userPhoto;
        TextView mFullName;
        TextView mRating;
        TextView mCodeLines;
        TextView mProjects;
        TextView mBio;

        Button mShowMore;

        private CustomClickListener mListener;

        public UserViewHolder(View itemView, CustomClickListener customClickListener) {
            super(itemView);

            userPhoto = (AspectRatioImageView) itemView.findViewById(R.id.user_photo_img);
            mFullName = (TextView)itemView.findViewById(R.id.user_full_name_txt);
            mRating = (TextView)itemView.findViewById(R.id.rating_txt);
            mCodeLines = (TextView)itemView.findViewById(R.id.code_lines_txt);
            mProjects = (TextView)itemView.findViewById(R.id.projects_txt);
            mBio = (TextView)itemView.findViewById(R.id.bio_txt);

            mListener = customClickListener;

            mShowMore = (Button) itemView.findViewById(R.id.btn_more_info);
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
