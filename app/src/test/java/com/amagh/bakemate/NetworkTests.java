package com.amagh.bakemate;

import android.util.Log;

import com.amagh.bakemate.utils.NetworkUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        String urlString = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

        try {
            String response = NetworkUtils.getHttpResponse(urlString);

            String errorNoNetworkResponse = "No response returned when connecting to " + urlString +
                    ". Have sufficient permissions been added to the Manifest?";
            assertNotNull(errorNoNetworkResponse, response);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
