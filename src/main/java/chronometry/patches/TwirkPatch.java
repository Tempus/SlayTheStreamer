package chronometry.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.gikk.twirk.Twirk;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@SpirePatch(clz = Twirk.class, method = "createResources")
public class TwirkPatch
{
    @SpireInsertPatch(locator = Locator.class, localvars = {"writer", "reader", "socket"})
    public static void Insert(Twirk __instance, @ByRef BufferedWriter[] writer, @ByRef BufferedReader[] reader, @ByRef Socket[] socket) throws IOException
    {
        if (!Loader.isModLoaded("bettertwitchmod"))
        {
            writer[0] = new BufferedWriter(new OutputStreamWriter(socket[0].getOutputStream(), StandardCharsets.UTF_8));
            reader[0] = new BufferedReader(new InputStreamReader(socket[0].getInputStream(), StandardCharsets.UTF_8));
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(Twirk.class, "outThread");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
        }
    }
}
