package org.ternlang.android.game;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ternlang.common.thread.ThreadPool;
import org.ternlang.core.scope.MapModel;
import org.ternlang.core.scope.Model;
import org.ternlang.studio.agent.ProcessAgent;
import org.ternlang.studio.agent.ProcessContext;
import org.ternlang.studio.agent.ProcessMode;
import org.ternlang.studio.agent.ProcessStore;
import org.ternlang.studio.agent.worker.store.WorkerStore;

import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;

public class Agent {

    private static final String TAG = Agent.class.getSimpleName();

    private final Configuration configuration;
    private final AtomicBoolean active;
    private final GameAgent game;
    private final Activity activity;
    private final Executor executor;

    public Agent(GameActivity activity) {
        this.configuration = new Configuration(activity);
        this.executor = new ThreadPool(1);
        this.game = new GameAgent(activity);
        this.active = new AtomicBoolean();
        this.activity = activity;
    }

    public void start() {
        try {
            if (active.compareAndSet(false, true)) {
                log();
                execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute() {
        try {
            final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            final Map<String, Object> map = new HashMap<String, Object>();
            final Model model = new MapModel(map);
            final AgentLog log = new AgentLog(TAG);
            final ProcessStore store = new WorkerStore(configuration.getRemoteAddress());
            final ProcessContext context = new ProcessContext(
                  ProcessMode.SERVICE,
                  store,
                  configuration.getProcessName(),
                  configuration.getThreadCount(),
                  configuration.getStackSize());
            final ProcessAgent agent = new ProcessAgent(
                    context,
                    configuration.getLogLevel());
            final Runnable task = new Runnable() {
               @Override
               public void run(){
                  Log.i(TAG, "Finished");
               }
            };
            StrictMode.setThreadPolicy(policy);
            map.put(configuration.getContextName(), activity);
            map.put(configuration.getGameName(), game);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        URI root = URI.create("http://" + configuration.getRemoteHost() + ":" + configuration.getRemotePort());
                        agent.start(root, task, model, log);
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting agent", e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log() {
        try {
            Log.i(TAG, "remote-address=" + configuration.getRemoteAddress());
            Log.i(TAG, "system-name=" + configuration.getSystemName());
            Log.i(TAG, "process-name=" + configuration.getProcessName());
            Log.i(TAG, "log-level=" + configuration.getLogLevel());
            Log.i(TAG, "thread-count=" + configuration.getThreadCount());
            Log.i(TAG, "stack-size=" + configuration.getStackSize());
            Log.i(TAG, "game-name=" + configuration.getGameName());
            Log.i(TAG, "context-name=" + configuration.getContextName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
