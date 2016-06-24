package com.techidea.updatemanager;

import android.content.ContentProviderOperation;
import android.content.Context;

import java.util.Map;

/**
 * Created by zchao on 2016/6/21.
 */
public final class UpdateOptions {

    private final String checkUrl;
    private final Context mContext;
    private final Map<String, String> postParam;

    public UpdateOptions(Builder builder) {
        this.checkUrl = builder.checkUrl;
        this.mContext = builder.context;
        this.postParam = builder.postParam;
    }

    public String getCheckUrl() {
        return checkUrl;
    }

    public String getParams() {
        return formatParams();
    }

    private String formatParams() {
        String param = "";
        if (postParam != null && !postParam.isEmpty()) {
            for (String key : postParam.keySet()) {
                param += key + "=" + postParam.get(key) + "&";
            }
        }
        if (param.length() > 1) {
            param.substring(0, param.length() - 1);
        }
        return param;
    }

    public static class Builder {
        private String checkUrl = null;
        private Map<String, String> postParam = null;
        private Context context = null;
        private boolean checkUpdate = false;
        private boolean checkPackageName = true;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder checkUrl(String checkUrl) {
            this.checkUrl = checkUrl;
            return this;
        }

        public Builder setParam(Map<String, String> postParam) {
            this.postParam = postParam;
            return this;
        }

        public Builder checkPackageName(boolean checkPackageName) {
            this.checkPackageName = checkPackageName;
            return this;
        }

        public UpdateOptions build() {
            return new UpdateOptions(this);
        }

    }
}
