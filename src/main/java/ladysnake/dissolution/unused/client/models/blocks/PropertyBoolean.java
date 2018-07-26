package ladysnake.dissolution.unused.client.models.blocks;

import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyBoolean implements IUnlistedProperty<Boolean> {

    private final String name;

    public PropertyBoolean(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Boolean value) {
        return true;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public String valueToString(Boolean value) {
        return value.toString();
    }

}
