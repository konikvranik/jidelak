/**
 * 
 */
package net.suteren.android.jidelak;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * @author jd39426
 *
 */
public class ErrorViewActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.errorview);

		Bundle params = getIntent().getExtras();
		params.get("exception");
		
		
		
		// TODO Auto-generated method stub
	}

}
