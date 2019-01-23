package com.robocon.leonardchin.mesov3;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ChatViewHolder extends RecyclerView.ViewHolder  {
  TextView leftText,rightText;
  ImageView imageView;

  public ChatViewHolder(View itemView){
    super(itemView);

    leftText = (TextView)itemView.findViewById(R.id.leftText);
    rightText = (TextView)itemView.findViewById(R.id.rightText);
    imageView = (ImageView) itemView.findViewById(R.id.img_view);

  }
}