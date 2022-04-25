package io.github.crumcreators.topocrafty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrablender.api.TerraBlenderApi;

public class Topocrafty implements TerraBlenderApi {
    public static final Logger LOGGER = LoggerFactory.getLogger("topocrafty");


    @Override
    public void onTerraBlenderInitialized() {
        LOGGER.info("onTerraBlenderInitialized");
    }
}
