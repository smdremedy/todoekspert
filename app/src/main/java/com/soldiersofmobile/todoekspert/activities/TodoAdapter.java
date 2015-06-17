package com.soldiersofmobile.todoekspert.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.soldiersofmobile.todoekspert.R;
import com.soldiersofmobile.todoekspert.Todo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by madejs on 17.06.15.
 */
class TodoAdapter extends BaseAdapter {


    private final LayoutInflater layoutInflater;

    public TodoAdapter(LayoutInflater layoutInflater) {

        this.layoutInflater = layoutInflater;
    }

    private ArrayList<Todo> arrayList = new ArrayList<>();

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Todo getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Timber.d("Pos:" + i + " view:" + view);

        if (getItemViewType(i) == 0) {
            return getView0(i, view, viewGroup);
        } else {
            return getView1(i, view, viewGroup);
        }

    }

    private View getView0(int i, View view, ViewGroup viewGroup) {
        View inflatedView = view;
        if (inflatedView == null) {
            inflatedView = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
        }

        ViewHolder viewHolder2 = (ViewHolder) inflatedView.getTag();
        if (viewHolder2 == null) {
            viewHolder2 = new ViewHolder(inflatedView);
            inflatedView.setTag(viewHolder2);
        }

        Todo todo = getItem(i);
        viewHolder2.itemContentTextView.setText(Boolean.toString(todo.isDone()));
        viewHolder2.itemDoneCheckBox.setChecked(todo.isDone());
        viewHolder2.itemDoneCheckBox.setText(todo.getContent());
        return inflatedView;
    }

    private View getView1(int i, View view, ViewGroup viewGroup) {
        View inflatedView = view;
        if (inflatedView == null) {
            inflatedView = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
        }

        ViewHolder viewHolder2 = (ViewHolder) inflatedView.getTag();
        if (viewHolder2 == null) {
            viewHolder2 = new ViewHolder(inflatedView);
            inflatedView.setTag(viewHolder2);
        }

        final Todo todo = getItem(i);
        viewHolder2.itemContentTextView.setText(Boolean.toString(todo.isDone()));
        viewHolder2.itemDoneCheckBox.setChecked(todo.isDone());
        viewHolder2.itemDoneCheckBox.setText(todo.getContent());

        inflatedView.setBackgroundResource(R.drawable.button_selector);
        return inflatedView;
    }

    public void addAll(List<Todo> results) {
        arrayList.addAll(results);
        notifyDataSetChanged();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.itemDoneCheckBox)
        CheckBox itemDoneCheckBox;
        @InjectView(R.id.itemContentTextView)
        TextView itemContentTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
