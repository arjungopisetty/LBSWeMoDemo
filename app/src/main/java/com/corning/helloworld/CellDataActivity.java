package com.corning.helloworld;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.widget.TextView;


public class CellDataActivity extends ActionBarActivity {

    TextView txtBaseStationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_data);

        TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);

        CellLocation location = tm.getCellLocation();

        int base_station_id = 0;

        txtBaseStationId = (TextView)findViewById(R.id.txtBaseStationId);

        if (location instanceof CdmaCellLocation) {
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation)location;
            base_station_id = cdmaCellLocation.getBaseStationId();
            txtBaseStationId.setText("Base Station ID: " + base_station_id);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cell_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
