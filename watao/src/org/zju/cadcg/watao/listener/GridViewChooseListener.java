package org.zju.cadcg.watao.listener;

import org.zju.cadcg.watao.adapter.GeneralGridViewAdapter;
import org.zju.cadcg.watao.fragment.ShapeFragment;
import org.zju.cadcg.watao.utils.GLManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class GridViewChooseListener implements OnItemClickListener {

	private ShapeFragment fragment;

	public GridViewChooseListener(ShapeFragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public void onItemClick(final AdapterView<?> adapterView, final View view, final int position,
			long id) {
		if (GLManager.pottery.varUsedForEllipseToRegular >= 0.9) {

			new AlertDialog.Builder(fragment.getActivity()).setMessage("您目前的作品将会被覆盖，是否继续").setPositiveButton("是", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					((GeneralGridViewAdapter)adapterView.getAdapter()).choose(view,position);
					fragment.loadShapeFromFile(position);
				}
			}).setNegativeButton("否", null).show();

		}else{
			((GeneralGridViewAdapter)adapterView.getAdapter()).choose(view,position);
			fragment.loadShapeFromFile(position);
		}
	}

}
