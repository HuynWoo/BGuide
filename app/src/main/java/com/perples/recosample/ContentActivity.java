package com.perples.recosample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by uk2014 on 2015-06-23.
 */
public class ContentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view);

        String intent = getIntent().getExtras().getString("id");

        TextView rId = (TextView)findViewById(R.id.region_uniqueID);
        rId.setText(intent);

        ImageView rImg = (ImageView)findViewById(R.id.region_image);

        if(intent == "D230")
            rImg.setImageResource(R.drawable.d230);
        if(intent == "D235")
            rImg.setImageResource(R.drawable.d235);
        if(intent == "D324")
            rImg.setImageResource(R.drawable.d324);
        if(intent == "D325")
            rImg.setImageResource(R.drawable.d325);

    }
}
