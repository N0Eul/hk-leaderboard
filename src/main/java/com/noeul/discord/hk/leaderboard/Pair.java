package com.noeul.discord.hk.leaderboard;

import java.io.Serializable;
import java.util.Objects;

public class Pair<L, R> implements Serializable {
	public final L left;
	public final R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public static <L, R> Pair<L, R> of(L left, R right) {
		return new Pair<>(left, right);
	}

	public L getLeft() { return left; }
	public R getRight() { return right; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
	}

	@Override
	public int hashCode() {
		return Objects.hash(left) ^ Objects.hashCode(right);
	}

	@Override
	public String toString() {
		return "(" +left + ", " + right + ')';
	}
}
