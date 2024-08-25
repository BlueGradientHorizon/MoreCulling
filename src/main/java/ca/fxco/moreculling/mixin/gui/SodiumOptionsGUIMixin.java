package ca.fxco.moreculling.mixin.gui;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.SodiumOptionPage;
import ca.fxco.moreculling.utils.CacheUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.widgets.FlatButtonWidget;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static ca.fxco.moreculling.utils.CompatUtils.IS_MODERNFIX_LOADED;

@Restriction(require = @Condition("sodium"))
@Mixin(SodiumOptionsGUI.class)
public class SodiumOptionsGUIMixin extends Screen {

    @Shadow(remap = false)
    @Final
    private List<OptionPage> pages;

    @Shadow(remap = false)
    private OptionPage currentPage;

    @Unique
    private OptionPage moreculling$moreCullingPage;
    @Unique
    private FlatButtonWidget moreculling$resetCacheButton;

    protected SodiumOptionsGUIMixin(Component title) {
        super(title);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void moreculling$addGuiAtInit(Screen prevScreen, CallbackInfo ci) {
        if (MoreCulling.CONFIG.enableSodiumMenu) {
            this.moreculling$moreCullingPage = SodiumOptionPage.moreCullingPage();
            this.pages.add(this.moreculling$moreCullingPage); // Inject sodium page for moreCulling
        }
    }


    @Inject(
            method = "rebuildGUI()V",
            at = @At("RETURN"),
            remap = false,
            require = 0
    )
    private void moreculling$addCacheRefreshButton(CallbackInfo ci) {
        if (IS_MODERNFIX_LOADED) {
            return;
        }
        if (MoreCulling.CONFIG.enableSodiumMenu && this.currentPage == this.moreculling$moreCullingPage) {
            // 325 is the last button (211) + width (100) plus padding (20 + 4)
            this.addRenderableWidget(this.moreculling$resetCacheButton = new FlatButtonWidget(
                    new Dim2i(this.width - 325, this.height - 30, 100, 20),
                    Component.translatable("moreculling.config.resetCache"),
                    () -> {
                        CacheUtils.resetAllCache();
                        this.moreculling$resetCacheButton.setEnabled(false);
                    }));
        }
    }
}
