package vos.personal.triplog;

import java.util.Calendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogActivity extends ActionBarActivity {

	public LogActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

	/*
	 * public void endTrip(View view) {
	 * 
	 * }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		private String FILENAME = "TRIP_LOG";
		private LogOperations log;
		private Runnable timeUpdater;
		private Handler timeUpdateHandler;
		private final int TIME_UPDATE_FREQ = 999;

		public PlaceholderFragment() {
			log = new LogOperations();
			timeUpdateHandler = new Handler();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_log, container,
					false);
			TextView systemDate = (TextView) Helper.locateControl(rootView,
					R.id.lblSystemDate);
			if (null != systemDate) {
				systemDate.setText(Helper.getDateTime());
			}

			EditText customTimeControl = ((EditText) Helper.locateControl(
					rootView, R.id.txtDate));
			if (null != customTimeControl) {
				customTimeControl.setText(Helper.getDateTime());
			}

			registerStartTripHandler(rootView);
			registerEndTripHandler(rootView);
			updateTimeAlways(rootView, systemDate);
			return rootView;
		}

		@Override
		public void onResume() {
			super.onResume();
			timeUpdateHandler.postDelayed(timeUpdater, TIME_UPDATE_FREQ);
		}

		@Override
		public void onStop() {
			super.onStop();
			timeUpdateHandler.removeCallbacks(timeUpdater);
		}

		private void updateTimeAlways(View container,
				final TextView controlToUpdate) {
			timeUpdater = new Runnable() {
				@Override
				public void run() {
					controlToUpdate.setText(Helper.getDateTime());
					timeUpdateHandler
							.postDelayed(timeUpdater, TIME_UPDATE_FREQ);
				}
			};
		}

		private void registerStartTripHandler(final View container) {
			Button control = (Button) container.findViewById(R.id.cmdStartTrip);
			control.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					StringBuffer startString = new StringBuffer();
					EditText startReadingControl = ((EditText) Helper
							.locateControl(container, R.id.txtStartReading));
					if (null != startReadingControl) {
						Editable startReading = startReadingControl.getText();
						if (null != startReading
								&& 0 < startReading.toString().trim().length()) {
							EditText customTimeControl = ((EditText) Helper
									.locateControl(container, R.id.txtDate));
							if (null != customTimeControl) {
								Editable customTime = customTimeControl
										.getText();
								if (null != customTime
										&& 0 < customTime.toString().trim()
												.length()) {
									startString.append(customTime.toString());
								} else {
									startString.append(Helper.getDateTime());
								}
							} else {
								startString.append(Helper.getDateTime());
							}
							startString.append("+");
							startString.append(startReading.toString());
							log.writeLine(FILENAME, " ");
							log.write(FILENAME, startString.toString());
							Helper.displayToast(getActivity(), view,
									getString(R.string.app_message_startTrip));

						} else {
							new AlertDialog.Builder(getActivity())
									.setTitle("Error")
									.setMessage("Give start reading!")
									.setNeutralButton("Close", null).show();
						}
					} else {
						new AlertDialog.Builder(getActivity())
								.setTitle("Error")
								.setMessage("Programmatic error!")
								.setNeutralButton("Close", null).show();
					}

				}
			});
		}

		private void registerEndTripHandler(final View container) {
			Button control = (Button) container.findViewById(R.id.cmdEndTrip);
			control.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

					StringBuffer endString = new StringBuffer();
					// endString.append(((EditText)Helper.locateControl(view,
					// R.id.txtEndDate)).getText());
					EditText endReadingControl = ((EditText) Helper
							.locateControl(container, R.id.txtEndReading));
					if (null != endReadingControl) {
						Editable endReading = endReadingControl.getText();
						if (null != endReading
								&& 0 < endReading.toString().trim().length()) {
							endString.append("+");
							endString.append(endReading.toString());
							endString.append("+");
							EditText customTimeControl = ((EditText) Helper
									.locateControl(container, R.id.txtDate));
							if (null != customTimeControl) {
								Editable customTime = customTimeControl
										.getText();
								if (null != customTime
										&& 0 < customTime.toString().trim()
												.length()) {
									endString.append(customTime.toString());
								} else {
									endString.append(Helper.getDateTime());
								}
							} else {
								endString.append(Helper.getDateTime());
							}
							endString.append("~");
							log.write(FILENAME, endString.toString());
							Helper.displayToast(getActivity(), view,
									getString(R.string.app_message_endTrip));
						} else {
							new AlertDialog.Builder(getActivity())
									.setTitle("Error")
									.setMessage("Give end reading!")
									.setNeutralButton("Close", null).show();
						}

					} else {
						new AlertDialog.Builder(getActivity())
								.setTitle("Error")
								.setMessage("Programmatic error!")
								.setNeutralButton("Close", null).show();

					}
				}
			});
		}

	}

	public static class Helper {
		public static String getDateTime() {
			Calendar currentDate = Calendar.getInstance();
			int date = currentDate.get(Calendar.DATE);
			int month = currentDate.get(Calendar.MONTH);
			int year = currentDate.get(Calendar.YEAR);
			int hour = currentDate.get(Calendar.HOUR);
			int minute = currentDate.get(Calendar.MINUTE);

			String formattedDate = (10 > date ? "0" + Integer.toString(date)
					: Integer.toString(date));
			String formattedMonth = (10 > month ? "0" + Integer.toString(month)
					: Integer.toString(month));
			String formattedHour = (String) (10 > hour ? "0"
					+ Integer.toString(hour) : Integer.toString(hour));
			String formattedMinute = (String) (10 > minute ? "0"
					+ Integer.toString(minute) : Integer.toString(minute));

			String nowDateTime = formattedDate + "-" + formattedMonth + "-"
					+ year + "+" + formattedHour
					+ ":" + formattedMinute;
			return nowDateTime;
		}

		public static View locateControl(View container, int controlId) {
			return container.findViewById(controlId);
		}

		public static void displayToast(FragmentActivity scenario,
				View parentView, String appMessageStarttrip) {
			Toast message = Toast.makeText(scenario, appMessageStarttrip,
					Toast.LENGTH_SHORT);
			message.show();
		}
	}
}
