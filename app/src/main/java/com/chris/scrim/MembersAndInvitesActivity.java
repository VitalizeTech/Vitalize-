package com.chris.scrim;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MembersAndInvitesActivity extends AppCompatActivity {
    private MembersAdapter groupMembersAdapter;
    private List<User> groupMembers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_and_invites);

        List<User> pendingListUsers = VitalizeApplication.getTestUsers();
        groupMembers = VitalizeApplication.getTestUsers();
        groupMembersAdapter = new MembersAdapter(this, R.layout.members, groupMembers);
        ((ListView) findViewById(R.id.pendingInvitationListView)).setAdapter(new PendingInvitesAdapter(this, R.layout.pending_invites, pendingListUsers));
        ((ListView) findViewById(R.id.membersListView)).setAdapter(groupMembersAdapter);
    }
    class MembersAdapter extends ArrayAdapter<User> {
        public MembersAdapter(Context context, int resource, List<User> users) {
            super(context, resource, users);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View userView = convertView;
            if(userView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                userView = layoutInflater.inflate(R.layout.members, null);
            }
            User user = getItem(position);
            ((TextView)userView.findViewById(R.id.name)).setText(user.firstName);
            ((ImageView)userView.findViewById(R.id.profileImage)).setImageResource(user.profileImage);
            ((TextView)userView.findViewById(R.id.vitalizeRep)).setText(user.vitalizeRep + "");
            return userView;
        }
    }
    class PendingInvitesAdapter extends ArrayAdapter<User> {
        private List<User> pendingInvites;
        public PendingInvitesAdapter(Context context, int resource, List<User> users) {
            super(context, resource, users);
            pendingInvites = users;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View userView = convertView;
            if(userView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                userView = layoutInflater.inflate(R.layout.pending_invites, null);
            }
            User user = getItem(position);
            ((TextView)userView.findViewById(R.id.name)).setText(user.firstName);
            ((ImageView)userView.findViewById(R.id.profileImage)).setImageResource(user.profileImage);
            ((TextView)userView.findViewById(R.id.vitalizeRep)).setText(user.vitalizeRep + "");
            View approveButton =  userView.findViewById(R.id.approveButton);
            approveButton.setTag(position);
            approveButton.findViewById(R.id.approveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    User approvedMember = pendingInvites.remove(index);
                    groupMembers.add(approvedMember);
                    groupMembersAdapter.notifyDataSetChanged();
                    notifyDataSetChanged();
                }
            });
            View rejectButton = userView.findViewById(R.id.rejectButton);
            rejectButton.setTag(position);
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pendingInvites.remove((int) v.getTag());
                    notifyDataSetChanged();
                }
            });
            return userView;
        }
    }
}
