package ga.adrielwalter.smartcandy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ga.adrielwalter.smartcandy.Adapter.TransitionAdapter;

/**
 * Created by Adriel on 1/25/2016.
 */
public class DetailActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_PARAM_ID = "place_id";
    private ListView mList;
    private ImageView mImageView;
    private TextView mTitle;
    private ImageButton mAddButton;
    private Product mProduct;
    int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mProduct = ProductData.productList().get(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        mList = (ListView) findViewById(R.id.list);
        mImageView = (ImageView) findViewById(R.id.productImage);
        mTitle = (TextView) findViewById(R.id.textView);
        mAddButton = (ImageButton) findViewById(R.id.btn_add);
        mAddButton.setOnClickListener(this);
        defaultColor = getResources().getColor(R.color.primary_dark);

        setUpAdapter();
        loadPlace();
        windowTransition();
        getPhoto();
    }

    private void setUpAdapter() {

    }

    private void loadPlace() {

        mTitle.setText(mProduct.name);
        mImageView.setImageResource(mProduct.getImageResourceId(this));
    }

    private void windowTransition() {
        getWindow().getEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                mAddButton.animate().alpha(1.0f);
                getWindow().getEnterTransition().removeListener(this);
            }
        });
    }

       private void getPhoto() {
        Bitmap photo = BitmapFactory.decodeResource(getResources(), mProduct.getImageResourceId(this));
        colorize(photo);
    }

    private void colorize(Bitmap photo) {
        Palette mPalette = Palette.from(photo).generate();
        applyPalette(mPalette);
    }

    private void applyPalette(Palette mPalette) {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);
    }
}
