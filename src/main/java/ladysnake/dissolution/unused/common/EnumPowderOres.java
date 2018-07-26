package ladysnake.dissolution.unused.common;

import ladysnake.dissolution.unused.api.INBTSerializableType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public enum EnumPowderOres {
    CINNABAR(Items.AIR);

    private final Item component;
    private final EnumPowderOres refinedPowder;
    private final Item depletedResidues;

    EnumPowderOres(Item component) {
        this(component, null, Items.AIR);
    }

    EnumPowderOres(Item component, EnumPowderOres refined, Item depleted) {
        this.component = component;
        this.refinedPowder = refined;
        this.depletedResidues = depleted;
    }

    public Item getComponent() {
        return component;
    }

    public EnumPowderOres getRefinedPowder() {
        return refinedPowder;
    }

    public Item getDepletedResidues() {
        return depletedResidues;
    }

    public static final INBTSerializableType.INBTTypeSerializer<EnumPowderOres> SERIALIZER = new INBTSerializableType.EnumNBTTypeSerializer<>(EnumPowderOres.class);

}
