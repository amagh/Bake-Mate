package com.amagh.bakemate;

import android.app.Instrumentation;
import android.util.Log;

import com.amagh.bakemate.utils.NetworkUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by hnoct on 6/27/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class NetworkTests {
    @Before
    public void setUp() {
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void testNetworkResponse() {
        String urlString = "http://go.udacity.com/android-baking-app-json";

        try {
            String response = NetworkUtils.getHttpResponse(urlString);

            String errorNoNetworkResponse = "No response returned when connecting to " + urlString +
                    ". Have sufficient permissions been added to the Manifest?";
            assertNotNull(response, errorNoNetworkResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
