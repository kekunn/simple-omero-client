/*
 *  Copyright (C) 2020 GReD
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.

 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package fr.igred.omero.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.igred.omero.Client;
import fr.igred.omero.ImageContainer;
import fr.igred.omero.sort.SortImageContainer;
import fr.igred.omero.sort.SortTagAnnotationContainer;
import fr.igred.omero.metadata.annotation.TagAnnotationContainer;
import omero.ServerError;
import omero.gateway.exception.DSAccessException;
import omero.gateway.exception.DSOutOfServiceException;
import omero.gateway.model.AnnotationData;
import omero.gateway.model.DataObject;
import omero.gateway.model.DatasetData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.TagAnnotationData;
import omero.model.IObject;
import omero.model.ProjectAnnotationLink;
import omero.model.ProjectAnnotationLinkI;
import omero.model.ProjectI;
import omero.model.TagAnnotationI;

/**
 * Class containing a ProjectData
 * Implements function using the Project contained
 */
public class ProjectContainer {
    ///ProjectData contained 
    private ProjectData project;

    /**
     * Get the ProjectData id
     * 
     * @return ProjectData id
     */
    public Long getId()
    {
        return project.getId();
    }

    /**
     * Get the ProjectData name
     * 
     * @return ProjectData name
     */
    public String getName()
    {
        return project.getName();
    }

    /**
     * Get the ProjectData description
     * 
     * @return ProjectData description
     */
    public String getDescription()
    {
        return project.getDescription();
    }

    /**
     * @return the ProjectData contained
     */
    public ProjectData getProject()
    {
        return project;
    }




    /**
     * Get all the datasets in the project available from OMERO.
     * 
     * @return Collection of DatasetContainer.
     */
    public List<DatasetContainer> getDatasets() {
        Collection<DatasetData> datasets = project.getDatasets();

        List<DatasetContainer> datasetsContainer = new ArrayList<DatasetContainer>(datasets.size());

        for (DatasetData dataset : datasets)
            datasetsContainer.add(new DatasetContainer(dataset));

        return datasetsContainer;
    }

    /**
     * Get the dataset with the specified name from OMERO
     * 
     * @param name name of the Dataset searched
     * 
     * @return List of dataset with the given name
     */
    public List<DatasetContainer> getDatasets(String name) {
        Collection<DatasetData> datasets = project.getDatasets();

        List<DatasetContainer> datasetsContainer = new ArrayList<DatasetContainer>(datasets.size());

        for(DatasetData dataset : datasets)
            if (dataset.getName().equals(name))
                datasetsContainer.add(new DatasetContainer(dataset));

        return datasetsContainer;
    }





    /**
     * Add a dataset to the project in OMERO.
     * Create the dataset.
     * 
     * @param client      The user
     * @param name        Dataset name
     * @param description Dataset description
     * 
     * @return The object saved in OMERO. 
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public DataObject addDataset(Client client, 
                                 String name, 
                                 String description)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        DatasetData datasetData = new DatasetData();
        datasetData.setName(name);
        datasetData.setDescription(description);

        DataObject  r = addDataset(client, datasetData);

        return r;
    }

    /**
     * Add a dataset to the project in OMERO.
     * 
     * @param client  The user
     * @param dataset Dataset to be added
     * 
     * @return The object saved in OMERO
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public DataObject addDataset(Client           client, 
                                 DatasetContainer dataset)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        DataObject r = addDataset(client, dataset.getDataset());

        return r;
    }

    /**
     * Private function.
     * Add a dataset to the project.
     * 
     * @param client      The user
     * @param datasetData Dataset to be added
     * 
     * @return The object saved in OMERO
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    private DataObject addDataset(Client      client, 
                                  DatasetData datasetData)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        datasetData.setProjects(Collections.singleton(project));
        DataObject r = client.getDm().saveAndReturnObject(client.getCtx(), datasetData);

        return r;
    }





    /**
     * Add a tag to the project in OMERO.
     * Create the tag.
     * 
     * @param client      The user
     * @param name        Tag Name
     * @param description Tag description
     * 
     * @return The object saved in OMERO. 
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public IObject addTag(Client client, 
                          String name, 
                          String description)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        TagAnnotationData tagData = new TagAnnotationData(name);
        tagData.setTagDescription(description);

        IObject r = addTag(client, tagData);
        return r;
    }

    /**
     * Add a tag to the project in OMERO.
     * 
     * @param client The user
     * @param tag    Tag to be added 
     * 
     * @return The object saved in OMERO. 
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public IObject addTag(Client                 client, 
                          TagAnnotationContainer tag)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        IObject r = addTag(client, tag.getTag());
        return r;
    }

    /**
     * Private function.
     * Add a tag to the project in OMERO.
     * 
     * @param client  The user
     * @param tagData Tag to be added
     * 
     * @return The object saved in OMERO. 
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    private IObject addTag(Client            client, 
                           TagAnnotationData tagData)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        ProjectAnnotationLink link = new ProjectAnnotationLinkI();
        link.setChild(tagData.asAnnotation());
        link.setParent(new ProjectI(project.getId(), false));

        IObject r = client.getDm().saveAndReturnObject(client.getCtx(), link);
        return r;
    }

    /**
     * Add multiple tag to the project in OMERO.
     * 
     * @param client The user
     * @param id     Id in OMERO of the tag to add
     * 
     * @return The objects saved in OMERO
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public IObject addTag(Client client, 
                          Long   id)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        ProjectAnnotationLink link = new ProjectAnnotationLinkI();
        link.setChild(new TagAnnotationI(id, false));
        link.setParent(new ProjectI(project.getId(), false));
        IObject r = client.getDm().saveAndReturnObject(client.getCtx(), link);

        return r;
    }

    /**
     * Add multiple tag to the project in OMERO.
     * 
     * @param client The user
     * @param tags   Array of TagAnnotationContainer to add 
     * 
     * @return The objects saved in OMERO
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public Collection<IObject> addTags(Client                    client, 
                                       TagAnnotationContainer... tags)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        Collection<IObject> objects = new ArrayList<IObject>();
        for(TagAnnotationContainer tag : tags) {
            IObject r = addTag(client, tag.getTag());
            objects.add(r);
        }
        
        return objects;
    }

    /**
     * Add multiple tag to the project in OMERO.
     * The tags id is used
     * 
     * @param client The user
     * @param ids    Array of tag id to add
     * 
     * @return The objects saved in OMERO
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public Collection<IObject> addTags(Client  client, 
                                       Long... ids)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        Collection<IObject> objects = new ArrayList<IObject>();
        for(Long id : ids) {
            IObject r = addTag(client, id);
            objects.add(r);
        }
        
        return objects;
    }

    /**
     * Get all tag linked to a project in OMERO
     * 
     * @param client The user
     * 
     * @return Collection of TagAnnotationContainer each containing a tag linked to the dataset
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public List<TagAnnotationContainer> getTags(Client client)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        List<Long> userIds = new ArrayList<Long>();
        userIds.add(client.getId());

        List<Class<? extends AnnotationData>> types = new ArrayList<Class<? extends AnnotationData>>();
        types.add(TagAnnotationData.class);

        List<AnnotationData> annotations = client.getMetadata().getAnnotations(client.getCtx(), project, types, userIds);

        List<TagAnnotationContainer> tags = new ArrayList<TagAnnotationContainer>();

        if(annotations != null) {
            for (AnnotationData annotation : annotations) {
                TagAnnotationData tagAnnotation = (TagAnnotationData) annotation;
                
                tags.add(new TagAnnotationContainer(tagAnnotation));
            }
        }

        Collections.sort(tags, new SortTagAnnotationContainer());
        return tags;
    }

    /**
     * Get all images in the dataset available from OMERO.
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     */
    private List<ImageContainer> purge(List<ImageContainer> images)
    {
        List<ImageContainer> purged = new ArrayList<ImageContainer>();

        for(ImageContainer image : images)
        {
            if(purged.isEmpty() || purged.get(purged.size() - 1).getId() != image.getId())
            {
                purged.add(image);
            }
        }

        return purged;
    }

    /**
     * Get all images in the project available from OMERO.
     * 
     * @param client The user
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     */
    public List<ImageContainer> getImages(Client client)
        throws 
            DSOutOfServiceException,
            DSAccessException
    {
        Collection<DatasetContainer> datasets = getDatasets();
        
        List<ImageContainer> imagesContainer = new ArrayList<ImageContainer>();

        for(DatasetContainer dataset : datasets) {
            imagesContainer.addAll(dataset.getImages(client));
        }

        Collections.sort(imagesContainer, new SortImageContainer());

        return purge(imagesContainer);
    }

    /**
     * Get all images in the project with a certain from OMERO.
     * 
     * @param client The user
     * @param name   Name searched
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     */
    public List<ImageContainer> getImages(Client client, 
                                          String name)
        throws 
            DSOutOfServiceException,
            DSAccessException
    {
        Collection<DatasetContainer> datasets = getDatasets();
        
        List<ImageContainer> imagesContainer = new ArrayList<ImageContainer>();

        for(DatasetContainer dataset : datasets) {
            imagesContainer.addAll(dataset.getImages(client, name));
        }

        Collections.sort(imagesContainer, new SortImageContainer());

        return purge(imagesContainer);
    }

    /**
     * Get all images in the project with a certain motif in their name from OMERO.
     * 
     * @param client The user
     * @param motif  Motif searched in an Image name
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     */
    public List<ImageContainer> getImagesLike(Client client, 
                                              String motif)
        throws 
            DSOutOfServiceException,
            DSAccessException
    {
        Collection<DatasetContainer> datasets = getDatasets();
        
        List<ImageContainer> imagesContainer = new ArrayList<ImageContainer>(datasets.size());

        for(DatasetContainer dataset : datasets) {
            imagesContainer.addAll(dataset.getImagesLike(client, motif));
        }

        Collections.sort(imagesContainer, new SortImageContainer());

        return purge(imagesContainer);
    }

    /**
     * Get all images in the project tagged with a specified tag from OMERO.
     * 
     * @param client The user
     * @param tag    TagAnnotationContainer containing the tag researched
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public List<ImageContainer> getImagesTagged(Client                 client, 
                                                TagAnnotationContainer tag)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException,
            ServerError
    {
        Collection<DatasetContainer> datasets = getDatasets();
        
        List<ImageContainer> imagesContainer = new ArrayList<ImageContainer>(datasets.size());

        for(DatasetContainer dataset : datasets) {
            imagesContainer.addAll(dataset.getImagesTagged(client, tag));
        }

        Collections.sort(imagesContainer, new SortImageContainer());

        return purge(imagesContainer);
    }

    /**
     * Get all images in the project tagged with a specified tag from OMERO.
     * 
     * @param client The user
     * @param tagId   Id of the tag researched
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public List<ImageContainer> getImagesTagged(Client client, 
                                                Long   tagId)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException, 
            ServerError
    {
        Collection<DatasetContainer> datasets = getDatasets();
        
        List<ImageContainer> imagesContainer = new ArrayList<ImageContainer>(datasets.size());

        for(DatasetContainer dataset : datasets) {
            imagesContainer.addAll(dataset.getImagesTagged(client, tagId));
        }

        Collections.sort(imagesContainer, new SortImageContainer());

        return purge(imagesContainer);
    }

    /**
     * Get all images in the project with a certain key
     * 
     * @param client The user
     * @param key    Name of the key researched
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public List<ImageContainer> getImagesKey(Client client, 
                                             String key)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        Collection<DatasetContainer> datasets = getDatasets();
        
        List<ImageContainer> imagesContainer = new ArrayList<ImageContainer>(datasets.size());

        for(DatasetContainer dataset : datasets) {
            imagesContainer.addAll(dataset.getImagesKey(client, key));
        }

        Collections.sort(imagesContainer, new SortImageContainer());

        return purge(imagesContainer);
    }

    /**
     * Get all images in the project with a certain key value pair from OMERO.
     * 
     * @param client The user
     * @param key    Name of the key researched
     * @param value  Value associated with the key
     * 
     * @return ImageContainer list
     * 
     * @throws DSOutOfServiceException Cannot connect to OMERO
     * @throws DSAccessException       Cannot access data
     * @throws ExecutionException      A Facility can't be retrieved or instancied
     */
    public List<ImageContainer> getImagesPairKeyValue(Client client, 
                                                      String key, 
                                                      String value)
        throws 
            DSOutOfServiceException,
            DSAccessException,
            ExecutionException
    {
        Collection<DatasetContainer> datasets = getDatasets();
        
        List<ImageContainer> imagesContainer = new ArrayList<ImageContainer>(datasets.size());

        for(DatasetContainer dataset : datasets) {
            imagesContainer.addAll(dataset.getImagesPairKeyValue(client, key, value));
        }

        Collections.sort(imagesContainer, new SortImageContainer());

        return purge(imagesContainer);
    }




    /**
     * Constructor of the ProjectContainer class.
     * 
     * @param p ProjectData to be contained
     */
    public ProjectContainer(ProjectData p) {
        project = p;
    }
}