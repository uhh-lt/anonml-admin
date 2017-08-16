package ml.anon.admin.rules;

import com.google.common.base.Joiner;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ml.anon.admin.BaseView;
import ml.anon.recognition.rulebased.api.model.RuleImpl;
import ml.anon.recognition.rulebased.api.resource.RuleResource;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
@Slf4j
public class RulesView extends BaseView {

  public static final String ID = "RULESVIEW";

  private Grid<RuleImpl> grid = new Grid<>();

  @Resource
  private RuleResource ruleResource;

  public RulesView() {
    super();

    RuleEditor editor = new RuleEditor(ruleResource, grid);
    VerticalLayout mainLayout = new VerticalLayout(grid, editor);
    mainLayout.setMargin(false);

    addComponent(mainLayout);

    grid.asSingleSelect().addValueChangeListener(e -> {
      if (e.getValue() != null) {
        editor.changeBoundRule((e.getValue()));
      }
    });

    mainLayout.setSizeFull();

    grid.setDataProvider(new RegExProvider());

    grid.addColumn(r -> BooleanUtils.toString(r.isActive(), "T", "F")).setCaption("Aktiv");
    Grid.Column<RuleImpl, String> name = grid.addColumn(RuleImpl::getName).setCaption("Name");
    Grid.Column<RuleImpl, ml.anon.anonymization.model.Label> label = grid
        .addColumn(RuleImpl::getLabel).setCaption("Label");
    Grid.Column<RuleImpl, Double> weight = grid.addColumn(RuleImpl::getWeight)
        .setCaption("Gewicht");
    Grid.Column<RuleImpl, String> regEx = grid
        .addColumn(r -> StringUtils.abbreviate(StringUtils.defaultString(r.getRegExp()), 50))
        .setCaption("RegEx");

    Grid.Column<RuleImpl, String> constraints = grid
        .addColumn(r -> Joiner.on(", ").join(r.getConstrains()))
        .setCaption("Constraints");

    Grid.Column<RuleImpl, String> deleteable = grid
        .addColumn(r -> BooleanUtils.toStringYesNo(!r.isCore()))
        .setCaption("Constraints");
    name.setExpandRatio(1);
    label.setExpandRatio(1);
    regEx.setExpandRatio(1);

    grid.setCaption("RegEx Regeln");

    grid.setSizeFull();
    editor.setWidth(100, Unit.PERCENTAGE);

  }

  @PostConstruct
  public void init() {

  }


  @Override
  public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    System.out.print(viewChangeEvent);
  }

  private class RegExProvider extends AbstractBackEndDataProvider<RuleImpl, Void> {

    @Override
    protected Stream<RuleImpl> fetchFromBackEnd(Query<RuleImpl, Void> query) {
      return ruleResource.findAll().stream();
    }

    @Override
    protected int sizeInBackEnd(Query<RuleImpl, Void> query) {
      return ruleResource.findAll().size();
    }
  }


}
