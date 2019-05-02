package chronometry.patches;

import chronometry.SlayTheStreamer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.shop.Merchant;

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