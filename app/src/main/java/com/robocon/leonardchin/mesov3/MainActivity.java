package com.robocon.leonardchin.mesov3;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.PlaceHolderView;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static android.R.color.white;

public class MainActivity extends AppCompatActivity implements AIListener {

    DatabaseReference ref;
    DatabaseReference ref2;
    FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder> adapter;
    Boolean flagFab = true;
    private EditText editText;
    private RecyclerView recyclerView;
    private RelativeLayout addBtn;
    private AIService aiService;
    TextToSpeech t1;
    String patient_name;

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;

    //private DatabaseReference mDataReference;
    private StorageReference imageReference;
    private StorageReference fileRef;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_main);

        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();

//        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setTitle("INSIGHTX");
//        toolbar.setTitleTextColor(Color.WHITE);
//        toolbar.setLogo(R.mipmap.insightx_foreground);
        //toolbar.setLogo(R.drawable.hospital_action);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
        startService(new Intent(getBaseContext(), MyService.class));

        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO }, 1);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        editText = (EditText)findViewById(R.id.editText);
        addBtn = (RelativeLayout)findViewById(R.id.addBtn);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);

        ref2 = FirebaseDatabase.getInstance().getReference();
        ref2.keepSynced(true);

        //7f5a7cab9c6e431fba917cfe59ed7517
        final AIConfiguration config = new AIConfiguration("47d0e794a9a54db6b68f345fda74fd1f",
                AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final ai.api.AIDataService aiDataService = new ai.api.AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String message = editText.getText().toString().trim();

                if (!message.equals("")) {

                    ChatMessage chatMessage = new ChatMessage(message, "user", null);
                    ref.child("chat").push().setValue(chatMessage);

                    aiRequest.setQuery(message);
                    new AsyncTask<AIRequest, Void, AIResponse>() {

                        @Override protected AIResponse doInBackground(AIRequest... aiRequests) {
                            final AIRequest request = aiRequests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }

                        @Override protected void onPostExecute(AIResponse response) {
                            if (response != null) {

                                Result result = response.getResult();
                                String reply = result.getFulfillment().getSpeech();

                                String str1 = new String("Ok Calling Jason Lim.");
                                String str2 = new String("Ok Calling Leonard Chin.");
                                String str3 = new String("special");
                                String str12 = new String("sasbi");
                                String str7 = new String("The patient is name");
                                if (str3.matches(reply)) {
                                    String str5 = "I found 2 engineers are available nearby." + System.getProperty("line.separator") + "1. Jason" + System.getProperty("line.separator") + "2. Leonard Chin" + System.getProperty("line.separator") + "Who do you wanted to call?";
                                    ChatMessage chatMessage = new ChatMessage(str5, "INSIGHTX", null);
                                    t1.speak(str5, TextToSpeech.QUEUE_FLUSH, null);
                                    ref.child("chat").push().setValue(chatMessage);
                                }else if(str7.matches(reply)){
                                    String str10 = reply+"\t"+patient_name+".";
                                    ChatMessage chatMessage = new ChatMessage(str10, "INSIGHTX", null);
                                    t1.speak(str10, TextToSpeech.QUEUE_FLUSH, null);
                                    ref.child("chat").push().setValue(chatMessage);
                                }else if(str12.matches(reply)){
                                    String str15 = "launching...";
                                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sas.android.bimobile");
                                    if (launchIntent != null) {
                                        startActivity(launchIntent);//null pointer check in case package name was not found
                                    }
                                    ChatMessage chatMessage = new ChatMessage(str15, "INSIGHTX", null);
                                    t1.speak(str15, TextToSpeech.QUEUE_FLUSH, null);
                                    ref.child("chat").push().setValue(chatMessage);
                                }else {
                                    ChatMessage chatMessage = new ChatMessage(reply, "INSIGHTX",null);
                                    t1.speak(reply, TextToSpeech.QUEUE_FLUSH, null);
                                    ref.child("chat").push().setValue(chatMessage);
                                    if (str1.matches(reply)) {
                                        i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0146012490"));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(i);
                                            }
                                        }, 1000);
                                    }
                                    if (str2.matches(reply)) {
                                        i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0168860870"));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(i);
                                            }
                                        }, 1000);
                                    }
                                }

                            }
                        }
                    }.execute(aiRequest);
                } else {
                    aiService.startListening();
                }

                editText.setText("");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = (ImageView) findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);

                if (s.toString().trim().length() != 0 && flagFab) {
                    ImageViewAnimatedChange(MainActivity.this, fab_img, img);
                    flagFab = false;
                } else if (s.toString().trim().length() == 0) {
                    ImageViewAnimatedChange(MainActivity.this, fab_img, img1);
                    flagFab = true;
                }
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(ChatMessage.class,
                R.layout.msglist, ChatViewHolder.class, ref.child("chat")) {
            @Override protected void populateViewHolder(ChatViewHolder viewHolder, ChatMessage model,
                                                        int position) {

                if (model.getMsgUser().equals("user")) {

                    viewHolder.rightText.setText(model.getMsgText());
                    viewHolder.imageView.setVisibility(View.GONE);
                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                } else {
                    viewHolder.rightText.setVisibility(View.GONE);
                    boolean isPhoto = model.getPhotoUrl() != null;
                    if (isPhoto) {
                        viewHolder.leftText.setVisibility(View.GONE);
                        viewHolder.imageView.setVisibility(View.VISIBLE);

                        Glide.with(viewHolder.imageView.getContext())
                                .load(model.getPhotoUrl())
                                .override(800, 400)
                                .into(viewHolder.imageView);
                    }else{
                        viewHolder.leftText.setVisibility(View.VISIBLE);
                        viewHolder.imageView.setVisibility(View.GONE);
                        viewHolder.leftText.setText(model.getMsgText());
                    }
                }
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 || (positionStart >= (msgCount - 1)
                        && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
            }

            @Override public void onAnimationRepeat(Animation animation) {
            }

            @Override public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {
                    }

                    @Override public void onAnimationRepeat(Animation animation) {
                    }

                    @Override public void onAnimationEnd(Animation animation) {
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
    Intent i=null;
    @Override public void onResult(ai.api.model.AIResponse response) {

        Result result = response.getResult();

        String message = result.getResolvedQuery();
        ChatMessage chatMessage0 = new ChatMessage(message, "user",null);
        ref.child("chat").push().setValue(chatMessage0);

        String reply = result.getFulfillment().getSpeech();
        //ChatMessage chatMessage = new ChatMessage(reply, "Health Sight",null);
        //ref.child("chat").push().setValue(chatMessage);

        String str1 = new String("Ok Calling Jason Lim.");
        String str2 = new String("Ok Calling Leonard Chin.");
        String str3 = new String("special");
        String str7 = new String("The patient is name");
        String str12 = new String("sasbi");
        if (str3.matches(reply)){
            String str5 = "I found 2 engineers are available nearby." + System.getProperty("line.separator") + "1. Jason" + System.getProperty("line.separator") + "2. Leonard Chin" + System.getProperty("line.separator") + "Who do you wanted to call?";
            ChatMessage chatMessage = new ChatMessage(str5, "INSIGHTX",null);
            t1.speak(str5, TextToSpeech.QUEUE_FLUSH, null);
            ref.child("chat").push().setValue(chatMessage);
        }else if(str7.matches(reply)){
            String str10 = reply+"\t"+patient_name+".";
            ChatMessage chatMessage = new ChatMessage(str10, "INSIGHTX", null);
            t1.speak(str10, TextToSpeech.QUEUE_FLUSH, null);
            ref.child("chat").push().setValue(chatMessage);
        }else if(str12.matches(reply)){
            String str15 = "launching...";
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.sas.android.bimobile");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }
            ChatMessage chatMessage = new ChatMessage(str15, "INSIGHTX", null);
            t1.speak(str15, TextToSpeech.QUEUE_FLUSH, null);
            ref.child("chat").push().setValue(chatMessage);
        }else {
            ChatMessage chatMessage = new ChatMessage(reply, "INSIGHTX",null);
            t1.speak(reply, TextToSpeech.QUEUE_FLUSH, null);
            ref.child("chat").push().setValue(chatMessage);
            if (str1.matches(reply)) {
                i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0146012490"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(i);
                    }
                }, 1000);
            }
            if (str2.matches(reply)) {
                i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0168860870"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(i);
                    }
                }, 1000);
            }
        }

    }

    @Override public void onError(ai.api.model.AIError error) {

    }

    @Override public void onAudioLevel(float level) {

    }

    @Override public void onListeningStarted() {

    }

    @Override public void onListeningCanceled() {

    }

    @Override public void onListeningFinished() {

    }

    @Override public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);

    }
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.clear:
                ref.child("chat").setValue(null);
                Toast.makeText(MainActivity.this, "Messages Cleared", Toast.LENGTH_SHORT).show();
                break;

            case R.id. linkern:
                ChatMessage chatMessage1 = new ChatMessage("The patient name is Chin Linn Kern.", "bot",null);
                ref.child("chat").push().setValue(chatMessage1);
                break;

            case R.id. Chin:
                ChatMessage chatMessage2 = new ChatMessage("The patient name is Chin Hung Vui.", "bot",null);
                ref.child("chat").push().setValue(chatMessage2);
                break;

            case R.id. Jason:
                ChatMessage chatMessage3 = new ChatMessage("The patient name is Jason Lim.", "bot",null);
                ref.child("chat").push().setValue(chatMessage3);
                break;

            case R.id. Escape:
                ref.child("controller").child("escape").setValue("0");
                break;

            case R.id.falldown:
                ref.child("controller").child("fall_down").setValue("0");
                break;

            case R.id.screenshot:
                ref.child("controller").child("screenshot").setValue("0");
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;}*/

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

    private void setupDrawer(){
        mDrawerView
                .addView(new DrawerHeader())
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PROFILE))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_REQUESTS))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_VIDEOCALL))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_HELP));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.insightx5_foreground)
                .setTitle("InsightX")
                .setMessage("Are you sure you want to exit InsightX?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLACK);

    }
}
