package chronometry.patches;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.*;

import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;

import com.gikk.twirk.*;
import com.gikk.twirk.events.*;
import com.gikk.twirk.types.users.*;
import com.gikk.twirk.types.twitchMessage.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.screens.mainMenu.*;
import com.badlogic.gdx.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.badlogic.gdx.math.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.controller.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.rooms.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.megacrit.cardcrawl.vfx.*;
import com.badlogic.gdx.graphics.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.unlock.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.shop.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import org.apache.logging.log4j.*;
import de.robojumper.ststwitch.*;
import de.robojumper.ststwitch.TwitchConnection;
import com.google.gson.Gson;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpRequestBuilder;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import chronometry.SlayTheStreamer;
import basemod.ReflectionHacks;
import chronometry.BossChoicePatch;

public class ShopkeeperNamesPatch {

	static String MerchantName = "";

    @SpirePatch(clz=Merchant.class, 
    	method=SpirePatch.CONSTRUCTOR,
    	paramtypez={
    		float.class,
    		float.class,
    		int.class
    	})
    public static class setupShopkeeperName { 
    	static String client_id = "ldy9d28m1ry8zvg1ucxow8akhuuiw0";
       	static String URL = "https://api.twitch.tv/helix/streams?game_id=496902";

        public static void Postfix(Merchant m, float x, float y, int newShopScreen) {

		    // HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

// curl -H 'Client-ID: ldy9d28m1ry8zvg1ucxow8akhuuiw0' \
// -X GET 'https://api.twitch.tv/helix/streams?game_id=496902'
		    
		  //   Net.HttpRequest httpRequest = requestBuilder.newRequest().method("GET").url(setupShopkeeperName.URL).header("Client-ID", setupShopkeeperName.client_id ).header("User-Agent", "curl/7.43.0").build();
		  //   Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener()
		  //   {
		  //     public void handleHttpResponse(Net.HttpResponse httpResponse) {
				// JsonValue root = new JsonReader().parse(httpResponse.getResultAsString());

				// String user_name = ""
				// while (user_name != "" && ) {
				// 	String user_name = root.child().child().user_name();
				// }

		  //     	// String jsondata = httpResponse.getResultAsString();
		  //     	// String usernames[] = jsondata.split('"user_name":"');

		  //     	// jsondata.index('"user_name":"');
		  //     	// jsondata.index('","');

		  //     	//     GsonBuilder gson = new GsonBuilder();

		  //     	//     Map o = (Map)(gson.create().fromJson(httpResponse.getResultAsString(), Object.class));

		  //     	//     // Collection<Object> streams = o.get("data").get(0).get("username");
		  //     	//     o.get("data").get(0).get("user_name");
		  //     }
		      
		  //     public void failed(Throwable t) {
		  //     }
		      

		  //     public void cancelled() {
		  //     }
		  //   });

			String names[] = SlayTheStreamer.config.getString("MerchantNames").split(",");
			ShopkeeperNamesPatch.MerchantName = names[(int)(Math.random() * names.length)];
        }
    }

    @SpirePatch(clz=Merchant.class, method="render")
    public static class renderMerchantName { 
	    public static void Postfix(Merchant m, final SpriteBatch sb) {
            sb.setColor(Settings.CREAM_COLOR);
            FontHelper.renderFontCentered(sb, FontHelper.tipHeaderFont, ShopkeeperNamesPatch.MerchantName, m.hb.cX + 8.0F, m.hb.y + m.hb.height, Settings.CREAM_COLOR);
	    }
	}
}