package io.github.buzzxu.spuddy.security.objects;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author xux
 * @date 2024年04月19日 21:07:38
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuRouters {
    private List<MenuRouter> routes;
    private String home;

    public static MenuRouters of(String home,List<Menu> menus){
        checkArgument(!Strings.isNullOrEmpty(home),"请设置主页面");
        MenuRouters routers = new MenuRouters();
        routers.setHome(home);
        routers.setRoutes(menus.stream().map(MenuRouters::to).toList());
        return routers;
    }

    // 假设这些常量在类的其他部分定义
    private static final String LAYOUT_BASE = "layout.base";
    private static final String VIEW_PREFIX = "view.";
    private static final String ROUTE_PREFIX = "route.";

    private static MenuRouter to(Menu menu){
        // 清理或验证路径以避免安全风险
        String path = !Strings.isNullOrEmpty(menu.getPath())? menu.getPath() : menu.getTarget();
        // 使用变量减少代码重复，并提高可读性
        boolean isRootMenu = menu.getParentId() == null || menu.getParentId() == 0;
        MenuRouter router = MenuRouter.builder()
                .name(path)
                .path("/" + path)
                .meta(MenuRouter.Meta.builder()
                        .title(menu.getName())
                        .icon(menu.getIcon())
                        .i18n(ROUTE_PREFIX + path)
                        .order(menu.getSort())
                        .build())
                .build();
        // 优化子菜单的处理
        if (menu.getChilds() != null && !menu.getChilds().isEmpty()) {
            if(isRootMenu){
                //如果是第一级
                router.setComponent(LAYOUT_BASE);
            }
            router.setChildren(menu.getChilds().stream().map(MenuRouters::to).toList());
        } else {
            router.setComponent(isRootMenu ? LAYOUT_BASE + "@view." + path : VIEW_PREFIX + path);
        }
        return router;
    }



}
