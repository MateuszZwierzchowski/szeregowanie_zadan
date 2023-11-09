package org.example;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JobScheduling {

    static class Job {
        int id;
        int processingTime;
        int readyTime;

        Job(int id, int processingTime, int readyTime) {
            this.id = id;
            this.processingTime = processingTime;
            this.readyTime = readyTime;
        }
    }

    public static void main(String[] args) {

        try {
            String inputFile = args[0];
            String outputFileName = "out" + inputFile.substring(2);

            List<Job> jobs = new ArrayList<>();
            int[][] setupTimes = readInput(inputFile, jobs);

            List<Job> scheduledJobs = scheduleJobs(jobs, setupTimes);

            writeOutput(outputFileName, scheduledJobs);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[][] readInput(String fileName, List<Job> jobs) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        Iterator<String> iterator = lines.iterator();

        int n = Integer.parseInt(iterator.next().trim());
        int[][] setupTimes = new int[n][n];

        for (int i = 0; i < n; i++) {
            String[] parts = iterator.next().trim().split(" ");
            int processingTime = Integer.parseInt(parts[0]);
            int readyTime = Integer.parseInt(parts[1]);
            jobs.add(new Job(i, processingTime, readyTime));
        }

        for (int i = 0; i < n; i++) {
            String[] parts = iterator.next().trim().split(" ");
            for (int j = 0; j < n; j++) {
                setupTimes[i][j] = Integer.parseInt(parts[j]);
            }
        }

        return setupTimes;
    }

    private static List<Job> scheduleJobs(List<Job> jobs, int[][] setupTimes) {
        jobs.sort(Comparator.comparingInt(job -> job.readyTime));
        List<Job> scheduledJobs = new ArrayList<>(jobs.size());
        PriorityQueue<Job> queue = new PriorityQueue<>(Comparator.comparingInt(job -> job.readyTime));
        queue.addAll(jobs);

        int currentTime = 0;
        while (!queue.isEmpty()) {
            Job job = queue.poll();
            currentTime = Math.max(currentTime, job.readyTime);
            if (!scheduledJobs.isEmpty()) {
                currentTime += setupTimes[scheduledJobs.get(scheduledJobs.size() - 1).id][job.id];
            }
            job.readyTime = currentTime + job.processingTime;
            scheduledJobs.add(job);
            currentTime += job.processingTime;
        }

        return scheduledJobs;
    }

    private static void writeOutput(String fileName, List<Job> scheduledJobs) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            int sumCompletionTimes = scheduledJobs.stream().mapToInt(job -> job.readyTime).sum();
            writer.write(String.valueOf(sumCompletionTimes));
            writer.newLine();
            for (Job job : scheduledJobs) {
                writer.write((job.id + 1) + " ");
            }
        }
    }
}

