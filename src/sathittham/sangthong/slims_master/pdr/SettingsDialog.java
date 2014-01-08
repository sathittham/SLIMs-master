package sathittham.sangthong.slims_master.pdr;

import java.text.DecimalFormat;
import sathittham.sangthong.slims_master.R;
import sathittham.sangthong.slims_master.MainActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * Low-Pass Linear Acceleration
 * Copyright (C) 2013, Kaleb Kircher - Boki Software, Kircher Engineering, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A special dialog for the settings of the application. Allows the user to
 * select what filters are plotted and set filter parameters.
 * 
 * @author Kaleb
 * @version %I%, %G%
 */
public class SettingsDialog extends Dialog implements
		NumberPicker.OnValueChangeListener, OnCheckedChangeListener
{
	private boolean showAndDevSetAlpha = false;
	private boolean showWikiSetAlpha = false;

	private boolean plotAndDev = false;
	private boolean plotWiki = false;

	private LayoutInflater inflater;

	private View settingsAndDevPlotView;
	private View settingsWikiPlotView;

	private View settingsAndDevDyanicAlphaView;
	private View settingsWikiDyanicAlphaView;

	private View settingsAndDevSetAlphaView;
	private View settingsWikiSetAlphaView;

	private View settingsAndDevToggleAlphaView;
	private View settingsWikiToggleAlphaView;

	private NumberPicker wikiAlphaNP;
	private NumberPicker andDevAlphaNP;

	private TextView wikiFilterTextView;
	private TextView wikiAlphaTextView;

	private TextView andDevFilterTextView;
	private TextView andDevAlphaTextView;

	private DecimalFormat df;

	private CheckBox wikiSetAlphaCheckBox;

	private CheckBox wikiPlotCheckBox;

	private CheckBox andDevSetAlphaCheckBox;

	private CheckBox andDevPlotCheckBox;

	private RelativeLayout andDevSetAlphaView;

	private RelativeLayout wikiSetAlphaView;

	private RelativeLayout andDevToggleAlphaView;

	private RelativeLayout wikiToggleAlphaView;

	private LowPassFilter lpfWiki;

	private LowPassFilter lpfAndDev;

	private float wikiAlpha;
	private float andDevAlpha;

	private MainActivity activity;

	/**
	 * Create a dialog.
	 * 
	 * @param context
	 *            The context.
	 * @param lpfWiki
	 *            The Wikipedia LPF.
	 * @param lpfAndDev
	 *            The Android Developer LPF.
	 */
	public SettingsDialog(MainActivity activity,
			final LowPassFilter lpfWiki, LowPassFilter lpfAndDev)
	{
		super(activity);

		this.activity = activity;

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.lpfWiki = lpfWiki;
		this.lpfAndDev = lpfAndDev;

		readPrefs();

		inflater = getLayoutInflater();

		View settingsView = inflater.inflate(R.layout.settings, null, false);

		LinearLayout layout = (LinearLayout) settingsView
				.findViewById(R.id.layout_settings_content);

		createWikiAlphaSettings();
		createAndDevPlotSettings();

		layout.addView(settingsWikiPlotView);
		layout.addView(settingsAndDevPlotView);

		this.setContentView(settingsView);

		df = new DecimalFormat("#.####");
	}

	@Override
	public void onStop()
	{
		super.onStop();

		writePrefs();
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal)
	{
		if (picker.equals(wikiAlphaNP))
		{
			wikiAlphaTextView.setText(df.format(newVal * 0.001));

			if (showWikiSetAlpha)
			{
				wikiAlpha = newVal * 0.001f;

				lpfWiki.setAlpha(wikiAlpha);
			}
		}

		if (picker.equals(andDevAlphaNP))
		{
			andDevAlphaTextView.setText(df.format(newVal * 0.001));

			if (showAndDevSetAlpha)
			{
				andDevAlpha = newVal * 0.001f;

				lpfAndDev.setAlpha(andDevAlpha);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (buttonView.equals(this.wikiPlotCheckBox))
		{
			if (isChecked)
			{
				plotWiki = true;

				showWikiAlphaSettings();
			}
			else
			{
				plotWiki = false;

				removeWikiAlphaSettings();
			}

			this.activity.setPlotLPFWiki(plotWiki);
		}

		if (buttonView.equals(this.wikiSetAlphaCheckBox))
		{
			if (isChecked)
			{
				showWikiSetAlpha = true;

				showWikiSetAlphaView();

				lpfWiki.setAlphaStatic(showWikiSetAlpha);
			}
			else
			{
				showWikiSetAlpha = false;

				removeWikiSetAlphaView();

				lpfWiki.setAlphaStatic(showWikiSetAlpha);
			}
		}

		if (buttonView.equals(this.andDevPlotCheckBox))
		{
			if (isChecked)
			{
				plotAndDev = true;

				showAndDevAlphaSettings();
			}
			else
			{
				plotAndDev = false;

				removeAndDevAlphaSettings();
			}

			this.activity.setPlotLPFAndDev(plotAndDev);
		}

		if (buttonView.equals(this.andDevSetAlphaCheckBox))
		{
			if (isChecked)
			{
				showAndDevSetAlpha = true;

				showAndDevSetAlphaView();

				lpfAndDev.setAlphaStatic(showAndDevSetAlpha);
			}
			else
			{
				showAndDevSetAlpha = false;

				removeAndDevSetAlphaView();

				lpfAndDev.setAlphaStatic(showAndDevSetAlpha);
			}
		}
	}

	private void createAndDevPlotSettings()
	{
		settingsAndDevPlotView = inflater.inflate(R.layout.settings_plot, null,
				false);

		settingsAndDevDyanicAlphaView = inflater.inflate(
				R.layout.settings_toggle_dynamic_alpha, null, false);

		andDevPlotCheckBox = (CheckBox) settingsAndDevPlotView
				.findViewById(R.id.check_box_plot);

		andDevPlotCheckBox.setOnCheckedChangeListener(this);

		if (plotAndDev)
		{
			andDevPlotCheckBox.setChecked(true);
		}
		else
		{
			andDevPlotCheckBox.setChecked(false);
		}

		andDevFilterTextView = (TextView) settingsAndDevPlotView
				.findViewById(R.id.label_filter_name);

		andDevFilterTextView.setText("LPFAndDev");

		showAndDevAlphaSettings();
	}

	/**
	 * Create the Wikipedia Settings.
	 */
	private void createWikiAlphaSettings()
	{
		settingsWikiPlotView = inflater.inflate(R.layout.settings_plot, null,
				false);

		settingsWikiDyanicAlphaView = inflater.inflate(
				R.layout.settings_toggle_dynamic_alpha, null, false);

		wikiPlotCheckBox = (CheckBox) settingsWikiPlotView
				.findViewById(R.id.check_box_plot);

		wikiPlotCheckBox.setOnCheckedChangeListener(this);

		if (plotWiki)
		{
			wikiPlotCheckBox.setChecked(true);
		}
		else
		{
			wikiPlotCheckBox.setChecked(false);
		}

		wikiFilterTextView = (TextView) settingsWikiPlotView
				.findViewById(R.id.label_filter_name);

		wikiFilterTextView.setText("LPFWiki");

		showWikiAlphaSettings();
	}

	/**
	 * Create the Android Developer Settings.
	 */
	private void showAndDevAlphaSettings()
	{
		if (plotAndDev)
		{
			if (settingsAndDevToggleAlphaView == null)
			{
				settingsAndDevToggleAlphaView = inflater.inflate(
						R.layout.settings_toggle_dynamic_alpha, null, false);
			}

			andDevSetAlphaCheckBox = (CheckBox) settingsAndDevToggleAlphaView
					.findViewById(R.id.check_box_static_alpha);

			andDevSetAlphaCheckBox.setOnCheckedChangeListener(this);

			if (showAndDevSetAlpha)
			{
				andDevSetAlphaCheckBox.setChecked(true);
			}
			else
			{
				andDevSetAlphaCheckBox.setChecked(false);
			}

			andDevToggleAlphaView = (RelativeLayout) settingsAndDevPlotView
					.findViewById(R.id.layout_toggle_values);

			andDevToggleAlphaView.removeAllViews();

			andDevToggleAlphaView.addView(settingsAndDevToggleAlphaView);
		}
	}

	/**
	 * Show the Android Developer Settings.
	 */
	private void showAndDevSetAlphaView()
	{
		if (showAndDevSetAlpha)
		{
			if (settingsAndDevSetAlphaView == null)
			{
				settingsAndDevSetAlphaView = inflater.inflate(
						R.layout.settings_filter_alpha, null, false);
			}

			andDevAlphaTextView = (TextView) settingsAndDevSetAlphaView
					.findViewById(R.id.value_alpha);
			andDevAlphaTextView.setText(String.valueOf(0.1));

			andDevAlphaNP = (NumberPicker) settingsAndDevSetAlphaView
					.findViewById(R.id.numberPicker1);
			andDevAlphaNP.setMaxValue(1000);
			andDevAlphaNP.setMinValue(0);
			andDevAlphaNP.setValue(100);

			andDevAlphaNP.setOnValueChangedListener(this);

			andDevSetAlphaView = (RelativeLayout) settingsAndDevPlotView
					.findViewById(R.id.layout_set_values);

			andDevSetAlphaView.addView(settingsAndDevSetAlphaView);
		}
	}

	private void showWikiAlphaSettings()
	{
		if (plotWiki)
		{
			if (settingsWikiToggleAlphaView == null)
			{
				settingsWikiToggleAlphaView = inflater.inflate(
						R.layout.settings_toggle_dynamic_alpha, null, false);
			}

			wikiSetAlphaCheckBox = (CheckBox) settingsWikiToggleAlphaView
					.findViewById(R.id.check_box_static_alpha);

			wikiSetAlphaCheckBox.setOnCheckedChangeListener(this);

			if (showWikiSetAlpha)
			{
				wikiSetAlphaCheckBox.setChecked(true);
			}
			else
			{
				wikiSetAlphaCheckBox.setChecked(false);
			}

			wikiToggleAlphaView = (RelativeLayout) settingsWikiPlotView
					.findViewById(R.id.layout_toggle_values);

			wikiToggleAlphaView.removeAllViews();

			wikiToggleAlphaView.addView(settingsWikiToggleAlphaView);
		}
	}

	/**
	 * Show the Wikipedia Settings.
	 */
	private void showWikiSetAlphaView()
	{
		if (showWikiSetAlpha)
		{
			if (settingsWikiSetAlphaView == null)
			{
				settingsWikiSetAlphaView = inflater.inflate(
						R.layout.settings_filter_alpha, null, false);
			}

			wikiAlphaTextView = (TextView) settingsWikiSetAlphaView
					.findViewById(R.id.value_alpha);
			wikiAlphaTextView.setText(String.valueOf(0.1));

			wikiAlphaNP = (NumberPicker) settingsWikiSetAlphaView
					.findViewById(R.id.numberPicker1);
			wikiAlphaNP.setMaxValue(1000);
			wikiAlphaNP.setMinValue(0);
			wikiAlphaNP.setValue(100);

			wikiAlphaNP.setOnValueChangedListener(this);

			wikiSetAlphaView = (RelativeLayout) settingsWikiPlotView
					.findViewById(R.id.layout_set_values);

			wikiSetAlphaView.addView(settingsWikiSetAlphaView);
		}
	}

	/**
	 * Remove the Wikipedia Settings.
	 */
	private void removeAndDevAlphaSettings()
	{
		if (!plotAndDev)
		{
			andDevToggleAlphaView = (RelativeLayout) settingsAndDevPlotView
					.findViewById(R.id.layout_toggle_values);

			andDevToggleAlphaView.removeAllViews();

			andDevToggleAlphaView.invalidate();
		}
	}

	/**
	 * Remove the Android Developer Settings.
	 */
	private void removeAndDevSetAlphaView()
	{
		if (!showAndDevSetAlpha)
		{
			andDevSetAlphaView = (RelativeLayout) settingsAndDevPlotView
					.findViewById(R.id.layout_set_values);

			andDevSetAlphaView.removeView(settingsAndDevSetAlphaView);

			settingsAndDevPlotView.invalidate();
		}
	}

	/**
	 * Remove the Wikipedia Settings.
	 */
	private void removeWikiAlphaSettings()
	{
		if (!plotWiki)
		{
			wikiToggleAlphaView = (RelativeLayout) settingsWikiPlotView
					.findViewById(R.id.layout_toggle_values);

			wikiToggleAlphaView.removeAllViews();

			wikiToggleAlphaView.invalidate();
		}
	}

	/**
	 * Remove the Wikipedia Settings.
	 */
	private void removeWikiSetAlphaView()
	{
		if (!showWikiSetAlpha)
		{
			wikiSetAlphaView = (RelativeLayout) settingsWikiPlotView
					.findViewById(R.id.layout_set_values);

			wikiSetAlphaView.removeView(settingsWikiSetAlphaView);

			settingsWikiPlotView.invalidate();
		}
	}

	/**
	 * Read in the current user preferences.
	 */
	private void readPrefs()
	{
		SharedPreferences prefs = this.getContext().getSharedPreferences(
				"lpf_prefs", Activity.MODE_PRIVATE);

		this.showWikiSetAlpha = prefs.getBoolean("show_alpha_lpf_wiki", false);
		this.showAndDevSetAlpha = prefs.getBoolean("show_alpha_lpf_and_dev",
				false);

		this.plotAndDev = prefs.getBoolean("plot_lpf_and_dev", false);
		this.plotWiki = prefs.getBoolean("plot_lpf_wiki", false);

		this.wikiAlpha = prefs.getFloat("lpf_wiki_alpha", 0.1f);
		this.andDevAlpha = prefs.getFloat("lpf_and_dev_alpha", 0.9f);
	}

	/**
	 * Write the preferences.
	 */
	private void writePrefs()
	{
		// Write out the offsets to the user preferences.
		SharedPreferences.Editor editor = this.getContext()
				.getSharedPreferences("lpf_prefs", Activity.MODE_PRIVATE)
				.edit();

		editor.putBoolean("show_alpha_lpf_wiki", this.showWikiSetAlpha);
		editor.putBoolean("show_alpha_lpf_and_dev", this.showAndDevSetAlpha);

		editor.putBoolean("plot_lpf_and_dev", this.plotAndDev);
		editor.putBoolean("plot_lpf_wiki", this.plotWiki);

		editor.putFloat("lpf_wiki_alpha", this.wikiAlpha);
		editor.putFloat("lpf_and_dev_alpha", this.andDevAlpha);

		editor.commit();
	}
}
