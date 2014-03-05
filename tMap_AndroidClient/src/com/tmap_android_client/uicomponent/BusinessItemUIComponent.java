package com.tmap_android_client.uicomponent;

import com.example.tmap_androidclient.R;
import com.tmap_android_client.datatransfer.BusinessItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusinessItemUIComponent extends LinearLayout{
	private TextView itemTitle, itemType, itemFloor;
	private ImageView itemLogo;
	public String imageURL;
	
	public BusinessItemUIComponent(Context context,/*AttributeSet attrs, */BusinessItem businessItem) {
        super(context/*, attrs*/);
        setOrientation(HORIZONTAL);
        
        this.setBackgroundResource(R.drawable.search_item_selector_bg);
        
		LayoutInflater.from(context).inflate(R.layout.business_item_conponent, this, true);
        itemTitle = (TextView) this.findViewById(R.id.item_title);
        itemType = (TextView) this.findViewById(R.id.item_type);
        itemFloor = (TextView) this.findViewById(R.id.item_floor);
        itemLogo = (ImageView) this.findViewById(R.id.item_logo);
        
        itemTitle.setText(businessItem.title);
        itemType.setText(businessItem.type);
        itemFloor.setText(businessItem.floor + context.getString(R.string.floor_unit));
        imageURL = businessItem.imageURL;
    }
	
	public void setImage(Bitmap image) {
		if (image != null) {
			this.itemLogo.setImageBitmap(image);
		}
	}
}
