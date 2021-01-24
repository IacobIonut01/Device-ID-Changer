package com.iacob.idchanger;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.iacob.idchanger.app_parser.ApplicationAdapter;
import com.iacob.idchanger.app_parser.ApplicationModel;
import com.iacob.idchanger.id_parser.rootCheck;
import com.iacob.idchanger.utils.AppPreferences;
import com.iacob.idchanger.utils.IDManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    static View.OnClickListener listener;
    static View.OnClickListener listenerReboot;
    View.OnClickListener no_listener;
    AppPreferences preferences;

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

    private static String getValue(String tag, Node element) {
        return element.getAttributes().getNamedItem(tag).getTextContent();
    }

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
        listener = view -> {
            fab.setIconResource(R.drawable.ic_randomize);
            fab.setText("Reboot");
            fab.setOnClickListener(listenerReboot);
            rootCheck.execute(IDManager.writeXMLToSystem(apps));
        };
        listenerReboot = view -> {
            rootCheck.execute("reboot");
        };
        fab.setOnClickListener(null);
    }

    public boolean checkForRoot() {
        return rootCheck.IAmRoot();
    }

    private ArrayList<ApplicationModel> getInstalledApps() {
        ArrayList<ApplicationModel> res = new ArrayList<>();
        try {
            InputStream is = new ByteArrayInputStream(rootCheck.read("cat /data/system/users/0/settings_ssaid.xml").getBytes());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            Document doc;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                String isStripped = total.toString().replace("<namespaceHashes />", "");
                InputStream stream = new ByteArrayInputStream(isStripped.getBytes(StandardCharsets.UTF_8));
                doc = dBuilder.parse(stream);
            } else {
                doc = dBuilder.parse(is);
            }
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