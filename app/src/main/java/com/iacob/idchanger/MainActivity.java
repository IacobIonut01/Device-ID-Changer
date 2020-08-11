package com.iacob.idchanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.iacob.idchanger.app_parser.ApplicationAdapter;
import com.iacob.idchanger.app_parser.ApplicationModel;
import com.iacob.idchanger.utils.ItemRecyclerSpacer;
import com.iacob.idchanger.utils.rootCheck;
import com.iacob.idchanger.utils.AppPreferences;
import com.iacob.idchanger.utils.IDManager;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.content.pm.PackageManager.GET_META_DATA;

public class MainActivity extends AppCompatActivity {

    static ArrayList<ApplicationModel> apps;
    static AppCompatActivity context;
    static RecyclerView recyclerView;
    static ApplicationAdapter adapter;
    static ExtendedFloatingActionButton fab;
    View.OnClickListener no_listener;
    static View.OnClickListener listener;
    static View.OnClickListener listenerReboot;
    static View.OnClickListener listenerList;
    AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        preferences = new AppPreferences(this);
        recyclerView = findViewById(R.id.apps_recycler);
        apps = getInstalledApps();
        adapter = new ApplicationAdapter(apps, getSupportFragmentManager());
        recyclerView.addItemDecoration(new ItemRecyclerSpacer(0, 0, 0, 256, apps.size() - 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        fab = findViewById(R.id.action_fab);
        ExpandableLayout exp = findViewById(R.id.expandable_layout);
        CoordinatorLayout.LayoutParams parms = (CoordinatorLayout.LayoutParams) exp.getLayoutParams();
        parms.width = fab.getWidth();
        exp.setLayoutParams(parms);
        fab.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                Timer timer = new Timer();
                long DELAY = 1500;
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                if (s.toString().equals("Root Access Granted"))
                                    MainActivity.this.runOnUiThread(() -> {
                                        fab.setIconResource(R.drawable.ic_add);
                                        fab.setText("Nothing to Apply");
                                    });
                            }
                        },
                        DELAY
                );
            }
        });
        fab.setText(checkForRoot() ? "Root Access Granted" : "Root Access denied");
        findViewById(R.id.noRootContainer).setVisibility(checkForRoot() ? View.GONE : View.VISIBLE);
        fab.setIconResource(R.drawable.ic_rootguard);
        no_listener = view -> {};
        listener = view -> {
            fab.setIconResource(R.drawable.ic_randomize);
            fab.setText("Reboot");
            fab.setOnClickListener(listenerReboot);
            IDManager.writeXMLToSystem(apps);
        };
        listenerReboot = view -> {
            rootCheck.execute("reboot");
        };
        listenerList = view -> {

        };
        fab.setOnClickListener(no_listener);
    }

    public static void updateApp(ApplicationModel model) {
        for (int i = 0; i < apps.size(); i++) {
            ApplicationModel app = apps.get(i);
            if (app.package_name.equals(model.package_name)) {
                apps.set(i, model);
                Toast.makeText(context, String.format("%s's ID has been changed", model.app_name), Toast.LENGTH_SHORT).show();
                recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(adapter);
                fab.setOnClickListener(listener);
            }
        }
        fab.setIconResource(R.drawable.ic_done);
        fab.setText("Apply Changes");
    }

    public static void updateApps(ArrayList<ApplicationModel> models) {
        for (int i = 0; i < apps.size(); i++) {
            ApplicationModel app = apps.get(i);
            for (int k = 0; k < models.size(); k++) {
                ApplicationModel mod_app = models.get(k);
                if (app.package_name.equals(mod_app.package_name)) {
                    apps.set(i, mod_app);
                }
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(listenerList);
        Toast.makeText(context, String.format("%s IDs has been changed", models.size()), Toast.LENGTH_SHORT).show();
        fab.setIconResource(R.drawable.ic_done);
        fab.setText("Apply Changes");
    }

    public boolean checkForRoot() {
        return rootCheck.IAmRoot();
    }

    private static String getValue(String tag, Node element) {
        return element.getAttributes().getNamedItem(tag).getTextContent();
    }

    private ArrayList<ApplicationModel> getInstalledApps() {
        ArrayList<ApplicationModel> res = new ArrayList<>();
        try {
            InputStream is = new ByteArrayInputStream(rootCheck.read("cat /data/system/users/0/settings_ssaid.xml").getBytes());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();
            NodeList nList = doc.getElementsByTagName("setting");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                ApplicationInfo apf = getPackageManager().getApplicationInfo(getValue("package", node), GET_META_DATA);
                ApplicationModel newInfo = new ApplicationModel();
                newInfo.app_name = apf.loadLabel(getPackageManager()).toString();
                newInfo.package_name = apf.packageName;
                newInfo.ID = getValue("value", node);
                newInfo.defID = getValue("defaultValue", node);
                newInfo.icon = apf.loadIcon(getPackageManager());
                ApplicationModel.ExtendedInfo extendedInfo = new ApplicationModel.ExtendedInfo();
                extendedInfo.defaultSysSet = getValue("defaultSysSet", node);
                extendedInfo.id = getValue("id", node);
                extendedInfo.tag = getValue("tag", node);
                extendedInfo.name = getValue("name", node);
                newInfo.extendedInfo = extendedInfo;
                res.add(newInfo);
            }
            res.sort((applicationModel, t1) -> applicationModel.app_name.compareToIgnoreCase(t1.app_name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}