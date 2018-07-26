package ladysnake.dissolution.unused.api;

import net.minecraft.nbt.NBTTagCompound;

/**
 * It's a stack, for distillate.
 *
 * @author Pyrofab
 */
public class DistillateStack extends GenericStack<DistillateTypes> {

    public static final DistillateStack EMPTY = (DistillateStack) GenericStack.<DistillateTypes>empty();

    public DistillateStack(DistillateTypes type, int size) {
        super(type == null ? DistillateTypes.UNTYPED : null, size);
    }

    /**
     * Constructor by copy
     *
     * @param toClone the distillate stack that this stack will copy
     */
    public DistillateStack(DistillateStack toClone) {
        this(toClone.type, toClone.stackSize);
    }

    /**
     * Reads a stack from an NBTTagCompound
     */
    public DistillateStack(NBTTagCompound compound) {
        super(compound, DistillateTypes.SERIALIZER);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || type == DistillateTypes.UNTYPED;
    }

    @Override
    public String toString() {
        return "DistillateStack [type=" + type + ", stackSize=" + stackSize + "]";
    }

}
