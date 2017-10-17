package com.gujjubites;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter adapter;
    private RecyclerViewPager mRecyclerView;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    private List<addNews> allNews = null;
    private List<addNews> newsShowing = null;
    private DatabaseReference mRef;
    private addNews an;
    private boolean initial_data_loaded = false;
    private boolean refresh_first_time = true;
    private List<String> bookmarks_key = null;
    private BookMarksDatabase db = new BookMarksDatabase(this);
    private List<BookMarks> bookMarks;
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        getSupportActionBar().hide();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        allNews = new ArrayList<>();
        newsShowing = new ArrayList<>();
        bookmarks_key = new ArrayList<>();
        bookMarks = new ArrayList<>();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mRef = FirebaseDatabase.getInstance().getReference().child("approved");
        mRecyclerView = (RecyclerViewPager) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);
        mRef.limitToLast(200).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allNews.clear();
                newsShowing.clear();
                bookmarks_key.clear();

                for (DataSnapshot addNewsSnapshot : dataSnapshot.getChildren()) {
                    an = addNewsSnapshot.getValue(addNews.class);
                    allNews.add(0, an);
                    bookmarks_key.add(0,addNewsSnapshot.getKey());
                }
                for (int i = 0; i < 5; i++) {
                    newsShowing.add(i, allNews.get(i));
                }
                adapter = new CustomSwiperAdapter(MainActivity.this, newsShowing);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        // to check whether first news is bookmarked or not
        if(db.getBookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition())))
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_blue_24dp);

        else
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_white_24dp);
        //to check whether the pages are bookmarked or not, as user scroll
        mRecyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int i, int i1) {

                if(db.getBookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition())))
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_blue_24dp);

                else
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_white_24dp);

                if ((newsShowing.size() - i1) < 5) {
                    if (newsShowing.size() != allNews.size()) {
                        newsShowing.add(newsShowing.size(), allNews.get(newsShowing.size()));

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (refresh_first_time) {
                    mRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (initial_data_loaded) {
                                an = dataSnapshot.getValue(addNews.class);
                                newsShowing.add(0, an);
                                bookmarks_key.add(0,dataSnapshot.getKey());
                            }
                            refresh_first_time = false;
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    initial_data_loaded = true;
                    adapter.notifyDataSetChanged();
                }
                mySwipeRefreshLayout.setRefreshing(false);

            }
        });

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                return true;
            }

        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    if (getSupportActionBar().isShowing())
                        getSupportActionBar().hide();
                    else
                        getSupportActionBar().show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_top:
                if(mRecyclerView.getCurrentPosition() > 10)
                        mRecyclerView.scrollToPosition(10);
                mRecyclerView.smoothScrollToPosition(0);
                //add the function to perform here
                return(true);
            case R.id.action_bookmark:
                addBookMarks();
                //add the function to perform here
                return(true);
            case R.id.action_share:
                shareIt();
                //add the function to perform here
                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            adapter = new CustomSwiperAdapter(MainActivity.this, newsShowing);
            mRecyclerView.setAdapter(adapter);
            // Handle the camera action
        } else if (id == R.id.bookmarks) {
                    bookMarks = db.getAllBookMarks();
                    if (bookMarks.size() <= 0) {
                        Toast.makeText(getApplicationContext(), "No Bookmarks added till now", Toast.LENGTH_SHORT).show();
                        item.setChecked(false);
                    }
                    else
                        populateBookMarks();
        } else if (id == R.id.settings) {

            Toast.makeText(getApplicationContext(), String.valueOf(db.getBookMarksCount()),Toast.LENGTH_LONG).show();

        } else if (id == R.id.about) {
            item.setChecked(false);
            showAbout();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void shareIt() {
        //sharing implementation here
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String path = newsShowing.get(mRecyclerView.getCurrentPosition()).getImg_url();
        String title = newsShowing.get(mRecyclerView.getCurrentPosition()).getTitle();
        String description = newsShowing.get(mRecyclerView.getCurrentPosition()).getDescription();
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "A bite from Gujjubites"
        + "\n------------\n"+ title + "\n--------------\n" + description + "\n-----------\n\n" + "download this app from wwww.google.com");
        startActivity(Intent.createChooser(sharingIntent, "Share News"));
    }

    private void showAbout() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawer(GravityCompat.START);
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.about_dialog);
            dialog.show();
    }

    private void addBookMarks()
    {
        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        if(db.getBookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition())))
        {
            db.deleteBookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition()));
            Toast.makeText(getApplicationContext(), "News Removed from Bookmark", Toast.LENGTH_SHORT).show();
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_white_24dp);
            adapter.notifyDataSetChanged();
        }
        else {
            db.addBookMarks(new BookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition())));
            Toast.makeText(getApplicationContext(), "Book Mark Added", Toast.LENGTH_SHORT).show();
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_blue_24dp);
            adapter.notifyDataSetChanged();
        }
        // Reading all contacts
    }

    private void populateBookMarks() {
            //String s = "";
            //for(int i=0; i < bookMarks.size(); i++)
            //    s = s + " " + bookMarks.get(i).getKey();
            //Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG).show();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
            final List<addNews> newsBookMarks = new ArrayList<>();
            for (int i = 0; i < bookMarks.size(); i++) {
                mRef.child("approved").child(bookMarks.get(i).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //for (DataSnapshot addNewsSnapshot : dataSnapshot.getChildren()) {
                        an = dataSnapshot.getValue(addNews.class);
                        newsBookMarks.add(0, an);
                        //}
                    }
                   @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                adapter = new CustomSwiperAdapter(MainActivity.this, newsBookMarks);
                mRecyclerView.setAdapter(adapter);
                mySwipeRefreshLayout.setRefreshing(true);
                mySwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
