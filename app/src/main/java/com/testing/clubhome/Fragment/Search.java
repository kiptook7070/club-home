package com.testing.clubhome.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.testing.clubhome.R;
import com.testing.clubhome.supporting.MyListAdapter;
import com.testing.clubhome.supporting.clubSearchAdapter;

import java.util.ArrayList;
import java.util.List;


public class Search extends Fragment {

    EditText searchbar;
    LinearLayout club,peoples,empty;
    View clubsBar,peoplesBar;
    ListView list;
    List<String> userList=new ArrayList();
    List<String> nameList=new ArrayList();
    List<String> jobList=new ArrayList();
    List<String> clubNameList=new ArrayList<>();
    List<String> clubList=new ArrayList<>();
    String searchingPeople;

    //Firebase initialization
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabase;
    DatabaseReference clubDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    MyListAdapter adapter;
    com.testing.clubhome.supporting.clubSearchAdapter clubSearchAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);

        searchbar=view.findViewById(R.id.search_bar);
        club=view.findViewById(R.id.club);
        peoples=view.findViewById(R.id.peoples);
        empty=view.findViewById(R.id.empty);
        clubsBar=view.findViewById(R.id.clubsBar);
        peoplesBar=view.findViewById(R.id.peoplesBar);
        list=view.findViewById(R.id.list);

        //getting FireBase Values
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getReference("UsersInfo");
        clubDatabase=firebaseDatabase.getReference("ClubsInfo");

        searchingPeople="club";

        clubsBar.setVisibility(View.VISIBLE);
        peoplesBar.setVisibility(View.INVISIBLE);

        //whole  list of club
        clubDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                clubNameList.clear();
                clubList.clear();
                for(DataSnapshot ds:task.getResult().getChildren()) {
                    boolean publicavail=ds.child("Assets").child("Privacy").getValue().toString().equals("Public");
                    if(publicavail) {
                        clubNameList.add(ds.child("Assets").child("club name").getValue().toString().toLowerCase());
                        clubList.add(ds.getKey());
                    }
                }
                getAllUsers();
            }
        });

        //whole list of the user
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                userList.clear();
                nameList.clear();
                jobList.clear();

                for(DataSnapshot ds:task.getResult().getChildren()) {
                    String userElement = "" + ds.getKey();
                    if (!userElement.equals(user.getUid())) {
                        userList.add(userElement);
                        nameList.add( ds.child("name").getValue().toString().toLowerCase());
                        jobList.add(ds.child("job").getValue().toString().toLowerCase());
                    }

                }

            }
        });

        club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchingPeople="club";

                clubsBar.setVisibility(View.VISIBLE);
                peoplesBar.setVisibility(View.INVISIBLE);

                String searchUser= (String) searchbar.getText().toString().trim();
                if(!searchUser.isEmpty()){
                    searchUsers(searchUser);
                }else{
                    getAllUsers();
                }
            }
        });

        peoples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchingPeople="account";

                clubsBar.setVisibility(View.INVISIBLE);
                peoplesBar.setVisibility(View.VISIBLE);
                String searchUser= (String) searchbar.getText().toString().trim();
                if(!searchUser.isEmpty()){
                    searchUsers(searchUser);
                }else{
                    getAllUsers();
                }
            }
        });

        //On search Edit Text Changed text
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchUser= (String) searchbar.getText().toString().trim();
                if(!TextUtils.isEmpty(searchUser)){
                    searchUsers(searchUser);
                }else {
                    getAllUsers();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchUser= (String) searchbar.getText().toString().trim();
                if(!TextUtils.isEmpty(searchUser)){
                    searchUsers(searchUser);
                }else {
                    getAllUsers();
                }

            }
        });

        return view;


    }

    private void getAllUsers() {
        if(searchingPeople.equals("account")){


            if (getActivity()!=null){
                adapter=new MyListAdapter(getActivity(),userList);
                list.setAdapter(adapter);
            }

            if(userList.isEmpty()){
                empty.setVisibility(View.VISIBLE);

                list.setVisibility(View.INVISIBLE);
            }else{

                list.setVisibility(View.VISIBLE);
                empty.setVisibility(View.INVISIBLE);
            }

        }else if(searchingPeople.equals("club")){
            if (getActivity()!=null){
                clubSearchAdapter=new clubSearchAdapter(getActivity(),clubList);
                list.setAdapter(clubSearchAdapter);
            }

            if(clubList.isEmpty()){
                empty.setVisibility(View.VISIBLE);
                list.setVisibility(View.INVISIBLE);
            }else{
                empty.setVisibility(View.INVISIBLE);

                list.setVisibility(View.VISIBLE);
            }

        }

    }

    private void searchUsers(String textWrote) {
        List<String> searchList=new ArrayList<String>();
        if(!TextUtils.isEmpty(textWrote)) {
            textWrote=textWrote.toLowerCase();
            if(searchingPeople.equals("account")){
                searchList.clear();


                for(int i=0;i<userList.size();i++){
                    if(nameList.get(i).contains(textWrote)||jobList.get(i).contains(textWrote)){
                        searchList.add(userList.get(i));
                    }
                }

                if (getActivity()!=null){
                    adapter = new MyListAdapter(getActivity(), searchList);
                    adapter.notifyDataSetChanged();
                    list.setAdapter(adapter);
                }

                if(searchList.isEmpty()){
                    empty.setVisibility(View.VISIBLE);

                    list.setVisibility(View.INVISIBLE);
                }else{

                    list.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.INVISIBLE);
                }

            }
            else if(searchingPeople.equals("club")){
                searchList.clear();
                for(int i=0;i<clubList.size();i++){
                    if(clubNameList.get(i).contains(textWrote)){
                        searchList.add(clubList.get(i));
                    }
                }
                if (getActivity()!=null){
                    clubSearchAdapter = new clubSearchAdapter(getActivity(), searchList);
                    clubSearchAdapter.notifyDataSetChanged();
                    list.setAdapter(clubSearchAdapter);
                }

                if(searchList.isEmpty()){
                    empty.setVisibility(View.VISIBLE);

                    list.setVisibility(View.INVISIBLE);
                }else{

                    list.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.INVISIBLE);
                }
            }

        }
        else{
            Log.i("position", "get all user");
            getAllUsers();
        }


    }
}