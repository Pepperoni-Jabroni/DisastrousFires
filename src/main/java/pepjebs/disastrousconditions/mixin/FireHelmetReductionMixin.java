package pepjebs.disastrousconditions.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pepjebs.disastrousconditions.DisastrousConditionsMod;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Mixin(Entity.class)
public class FireHelmetReductionMixin {

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    public boolean applyFireDamageReduction(Entity e, DamageSource ds, float f) {
        if (isEntityWearingFireHelmet(e)) {
            return e.damage(ds, 0.5F * f);
        } else {
            return e.damage(ds, f);
        }
    }

    private boolean isEntityWearingFireHelmet(Entity e) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(e.getArmorItems().iterator(),
                Spliterator.ORDERED), false).anyMatch(i -> i.getItem() ==
                Registry.ITEM.get(DisastrousConditionsMod.FIRE_HELMET));
    }
}
