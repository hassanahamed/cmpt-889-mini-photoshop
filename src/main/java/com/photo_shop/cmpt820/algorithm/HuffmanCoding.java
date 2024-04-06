package com.photo_shop.cmpt820.algorithm;

import java.util.PriorityQueue;

public class HuffmanCoding {

    public static double calculateAverageHuffmanCodeLength(int[] histogram, int totalPixels) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();

        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > 0) {
                queue.add(new HuffmanNode((char) i, histogram[i]));
            }
        }

        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();

            HuffmanNode sum = new HuffmanNode('-', left.frequency + right.frequency);
            sum.left = left;
            sum.right = right;
            queue.add(sum);
        }

        HuffmanNode root = queue.poll();
        return calculateAverageLength(root, 0) / totalPixels;
    }

    private static double calculateAverageLength(HuffmanNode node, int depth) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) {
            return node.frequency * depth;
        }
        return calculateAverageLength(node.left, depth + 1) + calculateAverageLength(node.right, depth + 1);
    }
}

