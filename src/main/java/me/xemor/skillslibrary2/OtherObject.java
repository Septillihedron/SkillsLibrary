package me.xemor.skillslibrary2;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public sealed interface OtherObject permits OtherObject.Empty, OtherObject.Target, OtherObject.Location, OtherObject.ItemStack {

    static OtherObject create() {
        return new Empty();
    }
    static OtherObject create(Entity target) {
        return new Target(target);
    }
    static OtherObject create(Location location) {
        return new Location(location);
    }
    static OtherObject create(ItemStack itemStack) {
        return new ItemStack(itemStack);
    }

    /**
     * @deprecated
     * only for this example
     * this object should be created higher up using those above
     */
    static OtherObject create(@Nullable Object otherObject) {
        if (otherObject == null) return new Empty();
        return switch (otherObject) {
            case Entity target -> new Target(target);
            case Location location -> new Location(location);
            case ItemStack itemStack -> new ItemStack(itemStack);
            default -> throw new IllegalStateException("Unexpected value for other object: " + otherObject);
        };
    }

    Mode getMode();

    record Empty() implements OtherObject {
        @Override
        public Mode getMode() {
            return Mode.SELF;
        }
    }
    record Target(Entity target) implements OtherObject {
        @Override
        public Mode getMode() {
            return Mode.OTHER;
        }
    }
    record Location(Location location) implements OtherObject {
        @Override
        public Mode getMode() {
            return Mode.LOCATION;
        }
    }
    record ItemStack(ItemStack itemStack) implements OtherObject {
        @Override
        public Mode getMode() {
            return Mode.ITEM;
        }
    }

}
