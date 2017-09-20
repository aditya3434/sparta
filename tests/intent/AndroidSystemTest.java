import static sparta.checkers.quals.FlowPermission.*;

import sparta.checkers.quals.*;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.content.ContextWrapper;
import android.provider.CalendarContract;

public class AndroidSystemTest extends Activity {
    public static final String key1 = "android.app.extra.DEVICE_ADMIN";
    public static  final String key2 = "android.app.extra.DEVICE_ADMIN";


    @Source({}) @Sink("BIND_DEVICE_ADMIN") String getValueOK() {
        return null;
    }
    
    @Source({}) @Sink({}) String getValueNotOK() {
        return null;
    }
    
    void startActivitySuccess() {
        @IntentMap(value={@Extra(key="android.app.extra.DEVICE_ADMIN",
                sink={BIND_DEVICE_ADMIN}),
                @Extra(key="android.app.extra.ADD_EXPLANATION",
                    sink={BIND_DEVICE_ADMIN})
                }) Intent senderIntent = new Intent();
        startActivity(senderIntent);
    }
    
    void startActivitySuccess2() {
        @IntentMap(value={@Extra(key="android.app.extra.DEVICE_ADMIN",
                sink={BIND_DEVICE_ADMIN}),
                @Extra(key="android.app.extra.ADD_EXPLANATION",
                    sink={BIND_DEVICE_ADMIN})
                }) Intent senderIntent = new Intent();
        senderIntent.putExtra(key2, getValueOK());
        startActivity(senderIntent);
    }
    
    void startActivityFail() {
        @IntentMap(value={@Extra(key="android.app.extra.DEVICE_ADMIN",
                sink={BIND_DEVICE_ADMIN}),
                @Extra(key="android.app.extra.ADD_EXPLANATION",
                    sink={BIND_DEVICE_ADMIN})
                })
                Intent senderIntent = new Intent();

        //:: error: (argument.type.incompatible)
        senderIntent.putExtra(key1, getValueNotOK());
        startActivity(senderIntent);
    }
    
    void startActivityFail2() {
        @IntentMap({@Extra(key = "android.app.extra.DEVICE_ADMIN", sink = {BIND_DEVICE_ADMIN}) })
        Intent senderIntent = new Intent();
        //:: error: (send.intent.missing.key)
        startActivity(senderIntent);
    }
    
    
}
