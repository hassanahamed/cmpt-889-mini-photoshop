package com.photo_shop.cmpt820.algorithm;

class HuffmanNode implements Comparable<HuffmanNode> {
    int frequency;
    char data;
    HuffmanNode left, right;

    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        left = null;
        right = null;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        return frequency - node.frequency;
    }
}
