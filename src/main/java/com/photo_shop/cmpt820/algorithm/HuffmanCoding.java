package com.photo_shop.cmpt820.algorithm;

import java.util.PriorityQueue;

public class HuffmanCoding {

    // Calculate average Huffman code length
    public static double calculateAverageHuffmanCodeLength(int[] histogram, int totalPixels) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();

        // Step 1: Create nodes for each symbol and add to priority queue
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > 0) {
                queue.add(new HuffmanNode((char) i, histogram[i]));
            }
        }

        // Step 2: Build Huffman Tree
        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();

            HuffmanNode sum = new HuffmanNode('-', left.frequency + right.frequency);
            sum.left = left;
            sum.right = right;
            queue.add(sum);
        }

        // Step 3: Calculate average code length
        HuffmanNode root = queue.poll();
        return calculateAverageLength(root, 0) / totalPixels;
    }

    // Helper method to recursively calculate the total code length (weighted by frequency)
    private static double calculateAverageLength(HuffmanNode node, int depth) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) {
            return node.frequency * depth;
        }
        return calculateAverageLength(node.left, depth + 1) + calculateAverageLength(node.right, depth + 1);
    }
}

