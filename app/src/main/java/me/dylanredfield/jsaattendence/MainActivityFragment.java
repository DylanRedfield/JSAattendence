package me.dylanredfield.jsaattendence;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {
    private View mView;
    private ProgressDialog mProgressDialog;
    private ArrayList<ParseObject> mMemberList;
    private ListView mListView;
    private MemberAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        mAdapter = new MemberAdapter();
        mListView = (ListView) mView.findViewById(R.id.list_view);
        queryList();
        mProgressDialog = new ProgressDialog(getActivity());
        return mView;
    }

    public void queryList() {
        ParseQuery<ParseObject> memberQuery = ParseQuery.getQuery(Keys.MEMBER_CLASS);
        memberQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                mMemberList = (ArrayList<ParseObject>) list;
                mAdapter.setList(mMemberList);
                mListView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.plus) {
            createNewMemberDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void createNewMemberDialog() {
        NewMemberDialog dialog = new NewMemberDialog();
        dialog.show(getActivity().getSupportFragmentManager(), null);

    }

    public class NewMemberDialog extends DialogFragment {

        public NewMemberDialog() {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            mView = inflater.inflate(R.layout.fragment_new_member, null);
            builder.setView(mView);

            final EditText editText = (EditText) mView.findViewById(R.id.edit_text);
            Button enter = (Button) mView.findViewById(R.id.enter);

            editText.setHint("Name");


            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ParseObject newUser = new ParseObject(Keys.MEMBER_CLASS);
                    newUser.put(Keys.NAME_STR, editText.getText().toString().trim());
                    newUser.put(Keys.MEETINGS_ATTENDED_NUM, 1);

                    mProgressDialog.show();
                    newUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            mProgressDialog.dismiss();

                            if (e == null) {
                                mMemberList.add(newUser);
                                mAdapter.notifyDataSetChanged();
                                getDialog().dismiss();
                            } else {
                                Helpers.showDialog("Whoops", e.getMessage(), getActivity());

                            }
                        }
                    });
                }
            });
            final Dialog dialog = builder.create();
            dialog.setTitle("New Member");

            return dialog;
        }
    }

    public class MemberAdapter extends BaseAdapter {
        ArrayList<ParseObject> mList;
        TextView mName;
        TextView mAttendance;

        public MemberAdapter() {
            mList = new ArrayList<>();
        }

        public void setList(ArrayList<ParseObject> list) {
            mList = list;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = getLayoutInflater(null).inflate(R.layout.row_member, null);
            }
            mName = (TextView)convertView.findViewById(R.id.name);
            mName.setText(mList.get(i).getString(Keys.NAME_STR));

            mAttendance = (TextView) convertView.findViewById(R.id.attendance);
            mAttendance.setText("Meetings attended: "
                    + mList.get(i).getNumber(Keys.MEETINGS_ATTENDED_NUM));

            return convertView;

        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
}
