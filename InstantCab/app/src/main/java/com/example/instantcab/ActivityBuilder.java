package com.example.instantcab;

import android.content.Context;
import android.content.Intent;

public class ActivityBuilder {
    public static final String DRIVER_DATA = "DRIVER_DATA";

    public static final String DRIVER_VEIW_TYPE = "RIVER_VEIW_TYPE";

    public static final int TYPE_DRIVER_VIEW = 0;

    public static final int TYPE_OTHER_VIEW = 1;

    public static void toDriverActivity(Context context, DriverBean bean, int type) {
        Intent intent = new Intent(context, DriverActivity.class);
        intent.putExtra(DRIVER_DATA,bean);
        intent.putExtra(DRIVER_VEIW_TYPE,type);
        context.startActivity(intent);
    }
}
