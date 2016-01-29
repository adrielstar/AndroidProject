package ga.adrielwalter.smartcandy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

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

    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static String address = "98:76:B6:00:64:A0";



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

        myBluetooth = BluetoothAdapter.getDefaultAdapter();



        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }


        mAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOnRelay();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "...In onResume - Attempting client connect...", Toast.LENGTH_LONG).show();

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = myBluetooth.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        myBluetooth.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Toast.makeText(getApplicationContext(), "...Connecting to Remote...", Toast.LENGTH_LONG).show();

        try {
            btSocket.connect();
            Toast.makeText(getApplicationContext(), "...Connection established and data link opened...", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(getApplicationContext(), "Fatal Error\", \"In onResume() and unable to close socket during connection failure"+ e2.getMessage() + ".", Toast.LENGTH_LONG).show();
            }
        }

        // Create a data stream so we can talk to server.
        Toast.makeText(getApplicationContext(), "...Creating Socket...", Toast.LENGTH_LONG).show();

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    // Turn relay on
    private void turnOnRelay()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    private void turnOffRelay()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "...In onPause()...", Toast.LENGTH_LONG).show();

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }


    private void errorExit(String title, String message){
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
    }



    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
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
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
}
