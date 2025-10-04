package dev.wuyuan.zerotierfix.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import dev.wuyuan.zerotierfix.api.model.ZtDevice;

/**
 * ZTncui API Client
 * Integrates with ztncui server for device management
 */
public class ZtncuiApiClient {
    private static final String TAG = "ZtncuiApiClient";
    private final String baseUrl;
    private String authToken;

    public ZtncuiApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    /**
     * Login to ztncui server
     */
    public boolean login(String username, String password) {
        try {
            URL url = new URL(baseUrl + "api/auth");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject credentials = new JSONObject();
            credentials.put("username", username);
            credentials.put("password", password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = credentials.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("token")) {
                    authToken = jsonResponse.getString("token");
                    return true;
                }
            }
            Log.e(TAG, "Login failed with response code: " + responseCode);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Login error", e);
            return false;
        }
    }

    /**
     * Get list of devices/members from a network
     */
    public List<ZtDevice> getDevices(String networkId) {
        List<ZtDevice> devices = new ArrayList<>();
        if (authToken == null) {
            Log.w(TAG, "Not authenticated");
            return devices;
        }

        try {
            URL url = new URL(baseUrl + "api/network/" + networkId + "/member");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + authToken);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject deviceObj = jsonArray.getJSONObject(i);
                    ZtDevice device = new ZtDevice();
                    
                    // Parse device info
                    device.setNodeId(deviceObj.optString("nodeId", ""));
                    device.setName(deviceObj.optString("name", "Unknown"));
                    device.setOnline(deviceObj.optBoolean("online", false));
                    
                    // Parse IP addresses
                    if (deviceObj.has("ipAssignments")) {
                        JSONArray ipArray = deviceObj.getJSONArray("ipAssignments");
                        List<String> ips = new ArrayList<>();
                        for (int j = 0; j < ipArray.length(); j++) {
                            ips.add(ipArray.getString(j));
                        }
                        device.setIpAddresses(ips);
                    }

                    devices.add(device);
                }
            } else {
                Log.e(TAG, "Failed to get devices, response code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting devices", e);
        }

        return devices;
    }

    /**
     * Check if client is authenticated
     */
    public boolean isAuthenticated() {
        return authToken != null;
    }

    /**
     * Logout and clear token
     */
    public void logout() {
        authToken = null;
    }
}
