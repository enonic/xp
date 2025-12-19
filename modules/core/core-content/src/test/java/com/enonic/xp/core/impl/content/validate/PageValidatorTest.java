package com.enonic.xp.core.impl.content.validate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ComponentConfigValidationError;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageValidatorTest
{
    private static final String COMPONENT_MIN_OCCURRENCES_I18N = "system.cms.validation.minOccurrencesInvalid.component";

    private static final String COMPONENT_MAX_OCCURRENCES_I18N = "system.cms.validation.maxOccurrencesInvalid.component";

    private static final String COMPONENT_OPTIONSET_OCCURRENCES_I18N = "system.cms.validation.optionsetOccurrencesInvalid.component";

    private PageDescriptorService pageDescriptorService;

    private PageTemplateService pageTemplateService;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private PageValidator validator;

    @BeforeEach
    void setUp()
    {
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.pageTemplateService = Mockito.mock( PageTemplateService.class );
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        this.validator = new PageValidator( pageDescriptorService, pageTemplateService, partDescriptorService, layoutDescriptorService );
    }

    @Test
    void page_without_descriptor_passes_validation()
    {
        final Page page = Page.create().build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void page_with_valid_config_passes_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final Form pageForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();
        final PageDescriptor pageDescriptor =
            PageDescriptor.create().key( pageDescriptorKey ).config( pageForm ).regions( RegionDescriptors.create().build() ).build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "title", "My Page Title" );

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( pageConfig ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void page_with_missing_required_config_fails_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final Form pageForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final PageDescriptor pageDescriptor =
            PageDescriptor.create().key( pageDescriptorKey ).config( pageForm ).regions( RegionDescriptors.create().build() ).build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final PropertyTree pageConfig = new PropertyTree();
        // Missing required 'title' field

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( pageConfig ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSize( 1 );
        assertMinOccurrenceError( validationErrors, "title", "title", "/", 1, 0 );
    }

    @Test
    void part_component_with_missing_required_config_fails_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final DescriptorKey partDescriptorKey = DescriptorKey.from( "myapp:mypart" );
        final Form partForm = Form.create()
            .addFormItem( Input.create().name( "heading" ).label( "Heading" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final PartDescriptor partDescriptor = PartDescriptor.create().key( partDescriptorKey ).config( partForm ).build();

        Mockito.when( partDescriptorService.getByKey( partDescriptorKey ) ).thenReturn( partDescriptor );

        final PropertyTree partConfig = new PropertyTree();
        // Missing required 'heading' field

        final PartComponent partComponent = PartComponent.create().descriptor( partDescriptorKey ).config( partConfig ).build();

        final Region region = Region.create().name( "main" ).add( partComponent ).build();
        final Regions regions = Regions.create().add( region ).build();

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSize( 1 );
        assertMinOccurrenceError( validationErrors, "heading", "heading", partComponent.getPath().toString(), 1, 0 );
    }

    @Test
    void layout_component_with_missing_required_config_fails_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "myapp:mylayout" );
        final Form layoutForm = Form.create()
            .addFormItem( Input.create().name( "columns" ).label( "Columns" ).inputType( InputTypeName.LONG ).required( true ).build() )
            .build();
        final LayoutDescriptor layoutDescriptor =
            LayoutDescriptor.create().key( layoutDescriptorKey ).config( layoutForm ).regions( RegionDescriptors.create().build() ).build();

        Mockito.when( layoutDescriptorService.getByKey( layoutDescriptorKey ) ).thenReturn( layoutDescriptor );

        final PropertyTree layoutConfig = new PropertyTree();
        // Missing required 'columns' field

        final LayoutComponent layoutComponent =
            LayoutComponent.create().descriptor( layoutDescriptorKey ).config( layoutConfig ).build();

        final Region region = Region.create().name( "main" ).add( layoutComponent ).build();
        final Regions regions = Regions.create().add( region ).build();

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSize( 1 );
        assertMinOccurrenceError( validationErrors, "columns", "columns", layoutComponent.getPath().toString(), 1, 0 );
    }

    @Test
    void nested_components_in_layout_are_validated()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "myapp:mylayout" );
        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create()
            .key( layoutDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( layoutDescriptorService.getByKey( layoutDescriptorKey ) ).thenReturn( layoutDescriptor );

        final DescriptorKey partDescriptorKey = DescriptorKey.from( "myapp:mypart" );
        final Form partForm = Form.create()
            .addFormItem( Input.create().name( "text" ).label( "Text" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final PartDescriptor partDescriptor = PartDescriptor.create().key( partDescriptorKey ).config( partForm ).build();

        Mockito.when( partDescriptorService.getByKey( partDescriptorKey ) ).thenReturn( partDescriptor );

        // Create a part component inside the layout with missing required config
        final PropertyTree partConfig = new PropertyTree();
        final PartComponent partComponent = PartComponent.create().descriptor( partDescriptorKey ).config( partConfig ).build();

        final Region layoutRegion = Region.create().name( "left" ).add( partComponent ).build();
        final Regions layoutRegions = Regions.create().add( layoutRegion ).build();

        final LayoutComponent layoutComponent =
            LayoutComponent.create().descriptor( layoutDescriptorKey ).config( new PropertyTree() ).regions( layoutRegions ).build();

        final Region pageRegion = Region.create().name( "main" ).add( layoutComponent ).build();
        final Regions pageRegions = Regions.create().add( pageRegion ).build();

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( pageRegions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSize( 1 );
        assertMinOccurrenceError( validationErrors, "text", "text", partComponent.getPath().toString(), 1, 0 );
    }

    @Test
    void page_template_with_valid_config_passes_validation()
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( "my-page-template" );
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final Form pageForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();
        final PageDescriptor pageDescriptor =
            PageDescriptor.create().key( pageDescriptorKey ).config( pageForm ).regions( RegionDescriptors.create().build() ).build();

        final Page templatePage = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).build();
        final PageTemplate pageTemplate = PageTemplate.newPageTemplate()
            .key( pageTemplateKey )
            .name( "my-template" )
            .parentPath( com.enonic.xp.content.ContentPath.ROOT )
            .page( templatePage )
            .build();

        Mockito.when( pageTemplateService.getByKey( pageTemplateKey ) ).thenReturn( pageTemplate );
        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "title", "My Page Title" );

        final Page page = Page.create().template( pageTemplateKey ).config( pageConfig ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void page_template_with_missing_required_config_fails_validation()
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( "my-page-template" );
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final Form pageForm = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
            .build();
        final PageDescriptor pageDescriptor =
            PageDescriptor.create().key( pageDescriptorKey ).config( pageForm ).regions( RegionDescriptors.create().build() ).build();

        final Page templatePage = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).build();
        final PageTemplate pageTemplate = PageTemplate.newPageTemplate()
            .key( pageTemplateKey )
            .name( "my-template" )
            .parentPath( com.enonic.xp.content.ContentPath.ROOT )
            .page( templatePage )
            .build();

        Mockito.when( pageTemplateService.getByKey( pageTemplateKey ) ).thenReturn( pageTemplate );
        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final PropertyTree pageConfig = new PropertyTree();
        // Missing required 'title' field

        final Page page = Page.create().template( pageTemplateKey ).config( pageConfig ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertTrue( validationErrors.hasErrors() );
        assertThat( validationErrors.stream() ).hasSize( 1 );
        assertMinOccurrenceError( validationErrors, "title", "title", "/", 1, 0 );
    }

    @Test
    void page_template_not_found_passes_validation()
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( "my-page-template" );

        Mockito.when( pageTemplateService.getByKey( pageTemplateKey ) ).thenReturn( null );

        final Page page = Page.create().template( pageTemplateKey ).config( new PropertyTree() ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void page_template_without_page_passes_validation()
    {
        final PageTemplateKey pageTemplateKey = PageTemplateKey.from( "my-page-template" );
        final PageTemplate pageTemplate = PageTemplate.newPageTemplate()
            .key( pageTemplateKey )
            .name( "my-template" )
            .parentPath( com.enonic.xp.content.ContentPath.ROOT )
            .build();

        Mockito.when( pageTemplateService.getByKey( pageTemplateKey ) ).thenReturn( pageTemplate );

        final Page page = Page.create().template( pageTemplateKey ).config( new PropertyTree() ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void page_descriptor_not_found_passes_validation() //TODO: discuss
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( null );

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void part_component_without_descriptor_passes_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final PartComponent partComponent = PartComponent.create().config( new PropertyTree() ).build();

        final Region region = Region.create().name( "main" ).add( partComponent ).build();
        final Regions regions = Regions.create().add( region ).build();

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void part_descriptor_not_found_passes_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final DescriptorKey partDescriptorKey = DescriptorKey.from( "myapp:mypart" );
        Mockito.when( partDescriptorService.getByKey( partDescriptorKey ) ).thenReturn( null );

        final PartComponent partComponent = PartComponent.create().descriptor( partDescriptorKey ).config( new PropertyTree() ).build();

        final Region region = Region.create().name( "main" ).add( partComponent ).build();
        final Regions regions = Regions.create().add( region ).build();

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void layout_component_without_descriptor_passes_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final LayoutComponent layoutComponent = LayoutComponent.create().config( new PropertyTree() ).build();

        final Region region = Region.create().name( "main" ).add( layoutComponent ).build();
        final Regions regions = Regions.create().add( region ).build();

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void layout_descriptor_not_found_passes_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( pageDescriptor );

        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "myapp:mylayout" );
        Mockito.when( layoutDescriptorService.getByKey( layoutDescriptorKey ) ).thenReturn( null );

        final LayoutComponent layoutComponent =
            LayoutComponent.create().descriptor( layoutDescriptorKey ).config( new PropertyTree() ).build();

        final Region region = Region.create().name( "main" ).add( layoutComponent ).build();
        final Regions regions = Regions.create().add( region ).build();

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void page_with_null_passes_validation()
    {
        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( null )
            .build();

        final ValidationErrors.Builder validationErrorsBuilder = ValidationErrors.create();
        validator.validate( params, validationErrorsBuilder );

        final ValidationErrors validationErrors = validationErrorsBuilder.build();
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    void part_component_with_exceeding_maximum_occurrence_fails_validation()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() ).build() );

        final DescriptorKey partDescriptorKey = DescriptorKey.from( "myapp:mypart" );
        final Form partForm = Form.create()
            .addFormItem(
                Input.create().name( "tagline" ).label( "Tagline" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() )
            .build();
        final PartDescriptor partDescriptor = PartDescriptor.create().key( partDescriptorKey ).config( partForm ).build();
        Mockito.when( partDescriptorService.getByKey( partDescriptorKey ) ).thenReturn( partDescriptor );

        final PropertyTree partConfig = new PropertyTree();
        partConfig.setString( "tagline[0]", "First" );
        partConfig.setString( "tagline[1]", "Second" );

        final PartComponent partComponent = PartComponent.create().descriptor( partDescriptorKey ).config( partConfig ).build();
        final Regions regions = Regions.create().add( Region.create().name( "main" ).add( partComponent ).build() ).build();
        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();
        validator.validate( params, builder );

        assertMaxOccurrenceError( builder.build(), "tagline", "tagline", partComponent.getPath().toString(), 1, 2 );
    }

    @Test
    void part_component_option_set_selection_violation_adds_error()
    {
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "myapp:mypage" );
        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( PageDescriptor.create()
            .key( pageDescriptorKey )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() ).build() );

        final DescriptorKey partDescriptorKey = DescriptorKey.from( "myapp:mypart" );
        final Form partForm = Form.create()
            .addFormItem( FormOptionSet.create()
                              .name( "cta" )
                              .multiselection( Occurrences.create( 1, 1 ) )
                              .addOptionSetOption( FormOptionSetOption.create().name( "primary" ).build() )
                              .addOptionSetOption( FormOptionSetOption.create().name( "secondary" ).build() )
                              .build() )
            .build();
        final PartDescriptor partDescriptor = PartDescriptor.create().key( partDescriptorKey ).config( partForm ).build();
        Mockito.when( partDescriptorService.getByKey( partDescriptorKey ) ).thenReturn( partDescriptor );

        final PropertyTree partConfig = new PropertyTree();
        partConfig.getRoot().addSet( "cta" ).addString( "_selected", "primary" );
        partConfig.getRoot().getSet( "cta" ).addString( "_selected", "secondary" );

        final PartComponent partComponent = PartComponent.create().descriptor( partDescriptorKey ).config( partConfig ).build();
        final Regions regions = Regions.create().add( Region.create().name( "main" ).add( partComponent ).build() ).build();
        final Page page = Page.create().descriptor( pageDescriptorKey ).config( new PropertyTree() ).regions( regions ).build();

        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().superType( ContentTypeName.structured() ).name( "myapp:mytype" ).build() )
            .page( page )
            .build();

        final ValidationErrors.Builder builder = ValidationErrors.create();
        validator.validate( params, builder );

        assertOptionSetOccurrenceError( builder.build(), "cta", "cta", partComponent.getPath().toString(), 1, 1, 2 );
    }

    private void assertMinOccurrenceError( final ValidationErrors validationErrors, final String expectedPropertyPath,
                                           final String expectedTarget, final String expectedComponentPath, final int expectedMin,
                                           final int actualCount )
    {
        final ValidationError error = validationErrors.stream().findFirst().orElseThrow();
        assertThat( error ).isInstanceOf( ComponentConfigValidationError.class );
        assertThat( error.getI18n() ).isEqualTo( COMPONENT_MIN_OCCURRENCES_I18N );
        assertThat( error.getArgs() ).containsExactly( expectedComponentPath, expectedTarget, expectedMin, actualCount );

        final ComponentConfigValidationError componentError = (ComponentConfigValidationError) error;
        assertThat( componentError.getPropertyPath().toString() ).isEqualTo( expectedPropertyPath );
        assertThat( componentError.getComponentPath().toString() ).isEqualTo( expectedComponentPath );
        assertThat( componentError.getApplicationKey() ).isEqualTo( ApplicationKey.from( "myapp" ) );
    }

    private void assertMaxOccurrenceError( final ValidationErrors validationErrors, final String expectedPropertyPath,
                                           final String expectedTarget, final String expectedComponentPath, final int expectedMax,
                                           final int actualCount )
    {
        final ValidationError error = validationErrors.stream().findFirst().orElseThrow();
        assertThat( error ).isInstanceOf( ComponentConfigValidationError.class );
        assertThat( error.getI18n() ).isEqualTo( COMPONENT_MAX_OCCURRENCES_I18N );
        assertThat( error.getArgs() ).containsExactly( expectedComponentPath, expectedTarget, expectedMax, actualCount );
    }

    private void assertOptionSetOccurrenceError( final ValidationErrors validationErrors, final String expectedPropertyPath,
                                                 final String expectedTarget, final String expectedComponentPath, final int min,
                                                 final int max, final int actual )
    {
        final ValidationError error = validationErrors.stream().findFirst().orElseThrow();
        assertThat( error ).isInstanceOf( ComponentConfigValidationError.class );
        assertThat( error.getI18n() ).isEqualTo( COMPONENT_OPTIONSET_OCCURRENCES_I18N );
        assertThat( error.getArgs() ).containsExactly( expectedComponentPath, expectedTarget, min, max, actual );

        final ComponentConfigValidationError componentError = (ComponentConfigValidationError) error;
        assertThat( componentError.getPropertyPath().toString() ).isEqualTo( expectedPropertyPath );
        assertThat( componentError.getComponentPath().toString() ).isEqualTo( expectedComponentPath );
        assertThat( componentError.getApplicationKey() ).isEqualTo( ApplicationKey.from( "myapp" ) );
    }
}
