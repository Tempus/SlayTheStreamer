package chronometry;

import java.util.ArrayList;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputAdapter;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import basemod.BaseMod;
import basemod.ModLabel;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ModButton;
import basemod.interfaces.*;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import chronometry.SlayTheStreamer;


public class ConfigPanel extends ModPanel {

    // Config junk
    private InputProcessor oldInputProcessor;

    public static final float BUTTON_X = 350.0f;
    public static final float BUTTON_Y = 650.0f;
    public static final float BUTTON_LABEL_X = 475.0f;
    public static final float BUTTON_LABEL_Y = 700.0f;
    public static final float BUTTON_ENABLE_X = 350.0f;
    public static final float BUTTON_ENABLE_Y = 600.0f;
    public static final float AUTOCOMPLETE_BUTTON_ENABLE_X = 350.0f;
    public static final float AUTOCOMPLETE_BUTTON_ENABLE_Y = 550.0f;
    public static final float AUTOCOMPLETE_LABEL_X = 350.0f;
    public static final float AUTOCOMPLETE_LABEL_Y = 425.0f;
    public static final float WHATMOD_BUTTON_X = 350.0f;
    public static final float WHATMOD_BUTTON_Y = 350.0f;

	public ConfigPanel() {
        ModLabel buttonLabel = new ModLabel("Adjust config in your LOCALAPPDATA/ModTheSpire folder", BUTTON_LABEL_X, BUTTON_LABEL_Y, this, (me) -> {
            // if (me.parent.waitingOnEvent) {
            //     me.text = "Press a numeric key";
            // } else {
            //     me.text = "Characters will start with " + Integer.toString(SlayTheStreamer.config.getInt("Energy")) + " energy";
            // }
        });
        this.addUIElement(buttonLabel);

        // ModButton consoleKeyButton = new ModButton(BUTTON_X, BUTTON_Y, this, (me) -> {
        //     me.parent.waitingOnEvent = true;
        //     oldInputProcessor = Gdx.input.getInputProcessor();
        //     Gdx.input.setInputProcessor(new InputAdapter() {
        //         @Override
        //         public boolean keyUp(int keycode) {

        //             int energy = -1;
        //             switch (keycode) {
        //                 case 7:
        //                 case 8:
        //                 case 9:
        //                 case 10:
        //                 case 11:
        //                 case 12:
        //                 case 13:
        //                 case 14:
        //                 case 15:
        //                 case 16:
        //                     energy = keycode - 7;
        //                     SlayTheStreamer.config.setInt("Energy", energy);
        //                     try {
        //                         SlayTheStreamer.config.save();
        //                     } catch (Exception e) {
        //                     }
        //                     me.parent.waitingOnEvent = false;
        //                     Gdx.input.setInputProcessor(oldInputProcessor);
        //                     return true;
        //             }

        //             return false;
        //         }
        //     });
        // });

        // this.addUIElement(consoleKeyButton);

        // ModLabeledToggleButton enableConsole = new ModLabeledToggleButton("Enable streak tracker",
        //         BUTTON_ENABLE_X, BUTTON_ENABLE_Y, Settings.CREAM_COLOR, FontHelper.charDescFont,
        //         SlayTheStreamer.config.getBool("Streak"), this, (label) -> {}, (button) -> {
        //             this.setBool("Streak", button.enabled);
        //         });
        // this.addUIElement(enableConsole);
        
                
        // ModLabeledToggleButton enableAutoComplete = new ModLabeledToggleButton("Enable Boot Value tracker",
        //         AUTOCOMPLETE_BUTTON_ENABLE_X, AUTOCOMPLETE_BUTTON_ENABLE_Y, Settings.CREAM_COLOR, FontHelper.charDescFont,
        //         SlayTheStreamer.config.getBool("Boot"), this, (label) -> {}, (button) -> {
        //             this.setBool("Boot", button.enabled);
        //         });
        // this.addUIElement(enableAutoComplete);
    }

    public void setBool(String key, Boolean value) {
        SlayTheStreamer.config.setBool(key, value);
        try {
            SlayTheStreamer.config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
