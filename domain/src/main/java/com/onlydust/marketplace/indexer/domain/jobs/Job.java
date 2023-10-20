package com.onlydust.marketplace.indexer.domain.jobs;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public abstract class Job implements Runnable {
    static Set<String> runningJobs = new HashSet<>();

    @Override
    public void run() {
        if (isRunning()) {
            LOGGER.info("Job {} is already running, skipping", name());
            return;
        }

        try {
            start();
        } catch (Exception e) {
            LOGGER.error("Job {} failed", name(), e);
        } finally {
            stop();
        }
    }

    private boolean isRunning() {
        return runningJobs.contains(name());
    }

    private void start() {
        LOGGER.info("Starting job {}", name());
        runningJobs.add(name());
        execute();
    }

    private void stop() {
        runningJobs.remove(name());
        LOGGER.info("Job {} finished", name());
    }

    public abstract void execute();

    public abstract String name();
}
