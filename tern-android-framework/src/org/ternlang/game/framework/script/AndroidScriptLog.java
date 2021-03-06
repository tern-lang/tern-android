package org.ternlang.game.framework.script;

import org.ternlang.studio.agent.log.ConsoleLog;
import org.ternlang.studio.agent.log.LogLevel;

import android.util.Log;

public class AndroidScriptLog extends ConsoleLog {

   private final String tag;
   
   public AndroidScriptLog(String tag) {
      this.tag = tag;
   }

   @Override
   public void log(LogLevel level, Object text) {
      if(level == LogLevel.TRACE) {
         Log.v(tag, String.valueOf(text));
      } else if(level == LogLevel.DEBUG) {
         Log.d(tag, String.valueOf(text));
      } else if(level == LogLevel.INFO) {
         Log.i(tag, String.valueOf(text));
      }
      super.log(level, text);
   }

   @Override
   public void log(LogLevel level, Object text, Throwable cause) { // trace must be enabled for this to work
      if(level == LogLevel.TRACE) {
         Log.v(tag, String.valueOf(text), cause);
      } else if(level == LogLevel.DEBUG) {
         Log.d(tag, String.valueOf(text), cause);
      } else if(level == LogLevel.INFO) {
         Log.i(tag, String.valueOf(text), cause);
      }
      super.log(level, text, cause);
   }
}
